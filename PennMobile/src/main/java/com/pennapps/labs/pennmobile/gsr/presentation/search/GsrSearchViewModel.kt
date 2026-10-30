package com.pennapps.labs.pennmobile.gsr.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.gsr.domain.FindAvailableGsrs
import com.pennapps.labs.pennmobile.gsr.domain.GSR_SLOT_MINUTES
import com.pennapps.labs.pennmobile.gsr.domain.GsrRoomOptions
import com.pennapps.labs.pennmobile.gsr.domain.GsrSearchResult
import com.pennapps.labs.pennmobile.gsr.domain.PENN_ZONE
import com.pennapps.labs.pennmobile.gsr.domain.durationOptions
import com.pennapps.labs.pennmobile.gsr.domain.groupByRoom
import com.pennapps.labs.pennmobile.gsr.domain.maxDurationMinutes
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.toGsrFailure
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val NO_LOCATIONS = "No GSR locations are available to your account."

sealed interface GsrContent {
    data object Loading : GsrContent

    data class Loaded(
        val result: GsrSearchResult,
        val rooms: List<GsrRoomOptions>,
    ) : GsrContent
}

/** Dates and times are campus-local ([PENN_ZONE]) because that's how GSR availability is organized. */
data class GsrSearchUiState(
    val today: LocalDate,
    val date: LocalDate,
    val startTime: LocalTime,
    val locations: List<GsrLocation> = emptyList(),
    val selectedLocation: GsrLocation? = null,
    val durationMinutes: Int = GSR_SLOT_MINUTES,
    val sortByTime: Boolean = true,
    val content: GsrContent = GsrContent.Loading,
    val isRefreshing: Boolean = false,
) {
    val durationOptions: List<Int> get() = selectedLocation?.durationOptions().orEmpty()

    /** Last day the date picker allows: [GsrLocation.bookableDays] counts today as day one. */
    val lastBookableDate: LocalDate
        get() {
            val extraDays = ((selectedLocation?.bookableDays ?: 1) - 1).coerceAtLeast(0)
            return today.plusDays(extraDays.toLong())
        }
}

private fun initialState(now: ZonedDateTime) =
    GsrSearchUiState(
        today = now.toLocalDate(),
        date = now.toLocalDate(),
        startTime = now.toLocalTime().truncatedTo(ChronoUnit.MINUTES),
    )

@HiltViewModel
class GsrSearchViewModel
    @Inject
    constructor(
        private val repo: GsrRepo,
        private val findAvailableGsrs: FindAvailableGsrs,
        private val clock: Clock,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(initialState(ZonedDateTime.now(clock).withZoneSameInstant(PENN_ZONE)))
        val uiState: StateFlow<GsrSearchUiState> = _uiState.asStateFlow()

        private var searchJob: Job? = null

        init {
            loadLocations()
        }

        fun selectLocation(location: GsrLocation) {
            repo.saveLastLocation(location)
            _uiState.update {
                it.copy(
                    selectedLocation = location,
                    durationMinutes = it.durationMinutes.coerceAtMost(location.maxDurationMinutes()),
                    date = it.date.coerceIn(it.today, it.copy(selectedLocation = location).lastBookableDate),
                )
            }
            search()
        }

        fun selectDate(date: LocalDate) {
            _uiState.update { it.copy(date = date.coerceIn(it.today, it.lastBookableDate)) }
            search()
        }

        fun selectTime(time: LocalTime) {
            _uiState.update { it.copy(startTime = time) }
            search()
        }

        fun selectDuration(minutes: Int) {
            _uiState.update { it.copy(durationMinutes = minutes) }
            search()
        }

        /** Re-sorts the results already on screen; no network call. */
        fun setSortByTime(sortByTime: Boolean) {
            _uiState.update { state ->
                val content = state.content
                state.copy(
                    sortByTime = sortByTime,
                    content = if (content is GsrContent.Loaded) content.copy(rooms = content.result.rooms(sortByTime)) else content,
                )
            }
        }

        /** Pull-to-refresh, retry, and "a booking just changed availability". */
        fun refresh() {
            if (_uiState.value.locations.isEmpty()) loadLocations() else search(isRefresh = true)
        }

        private fun loadLocations() {
            searchJob?.cancel()
            searchJob =
                viewModelScope.launch {
                    _uiState.update { it.copy(content = GsrContent.Loading) }
                    val locations =
                        try {
                            repo.getLocations()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            showResult(e.toGsrFailure())
                            return@launch
                        }
                    if (locations.isEmpty()) {
                        showResult(GsrSearchResult.Failed(NO_LOCATIONS))
                        return@launch
                    }
                    val saved = repo.lastLocationKey()
                    val selected = locations.firstOrNull { it.key == saved } ?: locations.first()
                    _uiState.update {
                        it.copy(
                            locations = locations,
                            selectedLocation = selected,
                            durationMinutes = it.durationMinutes.coerceAtMost(selected.maxDurationMinutes()),
                        )
                    }
                    runSearch()
                }
        }

        private fun search(isRefresh: Boolean = false) {
            if (_uiState.value.selectedLocation == null) return
            // A newer choice always wins; an in-flight search for the old filters is dropped.
            searchJob?.cancel()
            searchJob =
                viewModelScope.launch {
                    _uiState.update { if (isRefresh) it.copy(isRefreshing = true) else it.copy(content = GsrContent.Loading) }
                    runSearch()
                }
        }

        private suspend fun runSearch() {
            val state = _uiState.value
            val location = state.selectedLocation ?: return
            val from = ZonedDateTime.of(state.date, state.startTime, PENN_ZONE)
            showResult(findAvailableGsrs(location, from, state.durationMinutes))
        }

        private fun showResult(result: GsrSearchResult) {
            _uiState.update {
                it.copy(content = GsrContent.Loaded(result, result.rooms(it.sortByTime)), isRefreshing = false)
            }
        }

        private fun GsrSearchResult.rooms(sortByTime: Boolean): List<GsrRoomOptions> =
            if (this is GsrSearchResult.Found) options.groupByRoom(sortByTime) else emptyList()
    }
