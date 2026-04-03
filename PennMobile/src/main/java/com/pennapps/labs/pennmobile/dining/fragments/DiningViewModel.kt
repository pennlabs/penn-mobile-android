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

@HiltViewModel
class DiningViewModel
    @Inject
    constructor(
        private val sp: SharedPreferences,
        private val diningRepo: DiningRepo,
    ) : ViewModel() {
        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing

        private val _sortOrder = MutableStateFlow(DiningHallSortOrder.Residential)
        val sortOrder: StateFlow<DiningHallSortOrder> = _sortOrder

        private val _allDiningHalls = MutableStateFlow<List<DiningHall>>(emptyList())
        val allDiningHalls: StateFlow<List<DiningHall>> = _allDiningHalls

        private val _snackBarEvent = MutableStateFlow<SnackBarEvent>(SnackBarEvent.None)
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

        val favouriteDiningHallIds: StateFlow<List<Int>> = _favouriteDiningHalls

        val favouriteDiningHalls =
            _favouriteDiningHalls
                .combine(allDiningHalls) { favouriteIDs, halls ->
                    halls.filter { diningHall -> favouriteIDs.contains(diningHall.id) }
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

        fun refreshData() =
            viewModelScope.launch {
                if (!isRefreshing.value) {
                    _isRefreshing.value = true
                    fetchSortOrder()

                    Log.d("DiningViewModel", "Refreshing data: ${isRefreshing.value}")
                    diningRepo.fetchAllDiningHalls()
                    diningRepo.fetchFavouriteDiningHalls()

                    // Simulate a delay for the refresh operation. This delay has to be there otherwise, our refresh call won't work
                    // TODO: Remove this delay by making the fetch operations suspend
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

        fun setSortByMethod(diningHallSortOrder: DiningHallSortOrder) {
            sp.edit {
                putString("dining_sortBy", diningHallSortOrder.key)
            }

            fetchSortOrder()
            sortDiningHalls(allDiningHalls.value)
        }

        fun sortDiningHalls(halls: List<DiningHall>) {
            _allDiningHalls.value =
                halls.sortedWith { diningHall1, diningHall2 ->
                    DiningHallUtils.compareDiningHallsForSort(sortOrder.value, diningHall1, diningHall2)
                }
        }

        fun isFavourite(diningHall: DiningHall) = _favouriteDiningHalls.value.contains(diningHall.id)

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

        fun resetSnackBarEvent() {
            _snackBarEvent.value = SnackBarEvent.None
        }
    }
