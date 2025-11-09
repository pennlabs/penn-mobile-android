package com.pennapps.labs.pennmobile.dining.fragments

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder
import com.pennapps.labs.pennmobile.dining.classes.DiningHallUtils
import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiningViewModel @Inject constructor(
    private val sp: SharedPreferences,
    private val diningRepo: DiningRepo
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _sortOrder = MutableStateFlow(DiningHallSortOrder.Residential)
    val sortOrder: StateFlow<DiningHallSortOrder> = _sortOrder

    private val _allDiningHalls = MutableStateFlow<List<DiningHall>>(emptyList())
    val allDiningHalls: StateFlow<List<DiningHall>> = _allDiningHalls

    private val _favouriteDiningHalls = diningRepo.favouriteDiningHalls.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val favouriteDiningHalls = _favouriteDiningHalls
        .map { favouriteIDs -> allDiningHalls.value.filter { diningHall -> favouriteIDs.contains(diningHall.id)  }
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

    fun refreshData() = viewModelScope.launch {
        if (!isRefreshing.value) {
            _isRefreshing.value = true
            fetchSortOrder()

            diningRepo.fetchAllDiningHalls()
            diningRepo.fetchFavouriteDiningHalls()
            _isRefreshing.value = false
        }
    }

    private fun fetchSortOrder() {
        _sortOrder.value = DiningHallSortOrder.fromKey(
            sp.getString("dining_sortBy", DiningHallSortOrder.Residential.key)
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
        _allDiningHalls.value = halls.sortedWith { diningHall1, diningHall2 ->
            DiningHallUtils.compareDiningHallsForSort(sortOrder.value, diningHall1, diningHall2)
        }
    }

    fun isFavourite(diningHall: DiningHall) = _favouriteDiningHalls.value.contains(diningHall.id)

    fun toggleFavourite(diningHall: DiningHall) = viewModelScope.launch {
        if (isFavourite(diningHall)) {
            diningRepo.removeFromFavouriteDiningHalls(diningHall.id)
        } else {
            diningRepo.addToFavouriteDiningHalls(diningHall.id)
        }
    }


}