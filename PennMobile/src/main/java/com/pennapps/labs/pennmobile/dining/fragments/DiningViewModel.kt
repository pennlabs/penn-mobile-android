package com.pennapps.labs.pennmobile.dining.fragments

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.compose.utils.SnackBarEvent
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder
import com.pennapps.labs.pennmobile.dining.classes.DiningHallUtils
import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rx.schedulers.Schedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Manages the UI state for the dining screen.
 *
 * This ViewModel is responsible for fetching dining hall information, including a user's
 * favorite halls, and providing this data to the UI. It handles user interactions such as
 * sorting the dining hall list, marking halls as favorites, and refreshing the data.
 *
 * @property sp A [SharedPreferences] instance for persisting user preferences, such as the sort order.
 * @property diningRepo The repository responsible for fetching dining data from the network and local sources.
 */
@HiltViewModel
class DiningViewModel
    @Inject
    constructor(
        private val sp: SharedPreferences,
        private val diningRepo: DiningRepo,
    ) : ViewModel() {
        private val _isRefreshing = MutableStateFlow(false)

        /** Exposes the current refresh status to the UI, allowing for loading indicators to be shown. */
        val isRefreshing: StateFlow<Boolean> = _isRefreshing

        private val _sortOrder = MutableStateFlow(DiningHallSortOrder.Residential)

        /** The current method used for sorting the list of dining halls (both favorites and all dining halls). */
        val sortOrder: StateFlow<DiningHallSortOrder> = _sortOrder

        private val _allDiningHalls = MutableStateFlow<List<DiningHall>>(emptyList())

        /** A list of all available dining halls, sorted according to the current [sortOrder]. */
        val allDiningHalls: StateFlow<List<DiningHall>> = _allDiningHalls

        private val _snackBarEvent = MutableStateFlow<SnackBarEvent>(SnackBarEvent.None)

        /** Represents a one-time event to be shown in a SnackBar, e.g., for success or error messages. */
        val snackBarEvent: StateFlow<SnackBarEvent> = _snackBarEvent

        private val _menusByDate =
            MutableStateFlow<Map<String, List<DiningHall.Menu>>>(emptyMap())
        val menusByDate: StateFlow<Map<String, List<DiningHall.Menu>>> = _menusByDate

        private val _favouriteDiningHalls =
            diningRepo.favouriteDiningHalls.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList(),
            )

        /** A derived flow that contains the full [DiningHall] objects for the user's favorite venues.
         * Changes dynamically if: user likes or unlikes a dining hall
         *                         user changes the sort order
         * */
        val favouriteDiningHallIds: StateFlow<List<Int>> = _favouriteDiningHalls
        val favouriteDiningHalls =
            _allDiningHalls.combine(_favouriteDiningHalls) { allDiningHalls, favouriteDiningHalls ->
                allDiningHalls
                    .filter { diningHall -> favouriteDiningHalls.contains(diningHall.id) }
                    .sortedWith { a, b ->
                        DiningHallUtils.compareDiningHallsForSort(sortOrder.value, a, b)
                    }
            }

        init {
            fetchSortOrder()

            viewModelScope.launch {
                diningRepo.fetchAllDiningHalls()
                diningRepo.fetchFavouriteDiningHalls()

                diningRepo.allDiningHalls.collect { halls ->
                    val editableHalls = halls.toMutableList()
                    DiningHallUtils.getMenus(editableHalls)
                    sortDiningHalls(editableHalls)
                }
            }
        }

        /**
         * Triggers a refresh of all dining data from the repository.
         * It updates the [isRefreshing] state to notify the UI about the ongoing operation.
         * All operations are performed within the `viewModelScope`.
         */
        fun refreshData() =
            viewModelScope.launch {
                if (!isRefreshing.value) {
                    _isRefreshing.value = true
                    fetchSortOrder()

                    Log.d("DiningViewModel", "Refreshing data: ${isRefreshing.value}")
                    diningRepo.fetchAllDiningHalls()
                    diningRepo.fetchFavouriteDiningHalls()

//                // Simulate a delay for the refresh operation. This delay has to be there otherwise, our refresh call won't work
//                // TODO: Remove this delay by making the fetch operations suspend
                    delay(1000)
                    _isRefreshing.value = false
                    Log.d("DiningViewModel", "DoneRefreshing data: ${isRefreshing.value}")
                }
            }

        fun fetchMenusForWeek(hall: DiningHall) {
            _menusByDate.value = emptyMap()
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val mealOrder = listOf("Breakfast", "Brunch", "Lunch", "Dinner", "Express")

            for (offset in 0..6) {
                val dateStr = today.plusDays(offset.toLong()).format(fmt)
                try {
                    MainActivity.studentLifeInstance
                        .getMenus(dateStr)
                        .subscribeOn(Schedulers.io())
                        .subscribe({ menus ->
                            val dayMenus =
                                menus
                                    ?.filterNotNull()
                                    ?.filter { it.venue?.venueId == hall.id }
                                    ?.sortedWith { a, b ->
                                        mealOrder.indexOf(a.name) - mealOrder.indexOf(b.name)
                                    }
                                    ?: emptyList()

                            if (dayMenus.isNotEmpty()) {
                                _menusByDate.update { current ->
                                    current + (dateStr to dayMenus)
                                }
                            }
                        }, { throwable ->
                            Log.e("DiningViewModel", "Error fetching menus for $dateStr", throwable)
                        })
                } catch (e: Exception) {
                    Log.e("DiningViewModel", "Exception fetching menus for $dateStr", e)
                }
            }
        }

        private fun fetchSortOrder() {
            _sortOrder.value =
                DiningHallSortOrder.fromKey(
                    sp.getString("dining_sortBy", DiningHallSortOrder.Residential.key),
                )
            Log.d("DiningViewModel", "Sort order: ${sortOrder.value}")
        }

        /**
         * Updates the sorting preference for the dining hall list.
         * This new preference is persisted to [SharedPreferences] and the [allDiningHalls] list is resorted.
         *
         * @param diningHallSortOrder The new sorting method to be applied.
         */
        fun setSortByMethod(diningHallSortOrder: DiningHallSortOrder) {
            sp.edit {
                putString("dining_sortBy", diningHallSortOrder.key)
            }

            fetchSortOrder()
            sortDiningHalls(allDiningHalls.value)
        }

        /**
         * Sorts a given list of dining halls based on the current [sortOrder].
         *
         * @param halls The list of [DiningHall]s to be sorted.
         * @return A new list containing the sorted [DiningHall]s.
         */

        fun sortDiningHalls(halls: List<DiningHall>) {
            _allDiningHalls.value =
                halls.sortedWith { diningHall1, diningHall2 ->
                    DiningHallUtils.compareDiningHallsForSort(sortOrder.value, diningHall1, diningHall2)
                }
        }

        /**
         * Checks if a specific dining hall is marked as a favorite.
         *
         * @param diningHall The dining hall to check.
         * @return `true` if the dining hall is a favorite, `false` otherwise.
         */
        fun isFavourite(diningHall: DiningHall) = _favouriteDiningHalls.value.contains(diningHall.id)

        /**
         * Toggles the favorite status of a dining hall.
         * This function communicates with the [diningRepo] to update the backend and local data.
         * On completion, it posts a [SnackBarEvent] to inform the user of the result.
         *
         * @param diningHall The dining hall whose favorite status will be toggled.
         */
        fun toggleFavourite(diningHall: DiningHall) =
            viewModelScope.launch {
                val isFavourite = isFavourite(diningHall)

                val networkResult =
                    if (isFavourite) {
                        diningRepo.removeFromFavouriteDiningHalls(diningHall.id)
                    } else {
                        diningRepo.addToFavouriteDiningHalls(diningHall.id)
                    }

                Log.d("DiningViewModel", "Toggling favourite: networkResult is $networkResult")

                if (networkResult.isSuccessful) {
                    _snackBarEvent.value =
                        SnackBarEvent.Success(
                            message =
                                if (isFavourite) {
                                    "${diningHall.name} removed from favourites"
                                } else {
                                    "${diningHall.name} added to favourites"
                                },
                        )
                } else if (networkResult is Result.Error) {
                    _snackBarEvent.value = SnackBarEvent.Error(networkResult.message)
                }
            }

        /**
         * Resets the [snackBarEvent] to its default state.
         * This should be called by the UI after a snackbar event has been handled, to prevent it from
         * being shown again on configuration changes.
         */
        fun resetSnackBarEvent() {
            _snackBarEvent.value = SnackBarEvent.None
        }
    }
