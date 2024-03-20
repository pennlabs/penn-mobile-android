package com.pennapps.labs.pennmobile.viewmodels

import StudentLife
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomFavorites
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LaundryViewModel : ViewModel() {
    private var loadedRooms = false

    private var hasPreferences = false
    private val _favoriteRooms = MutableLiveData(LaundryRoomFavorites())

    val favoriteRooms : LiveData<LaundryRoomFavorites>
        get() = _favoriteRooms

    private val favoritesMutex = Mutex()

    private fun updateFavorites() {
        // kinda clown, but rebind so the observer can observe a change
        _favoriteRooms.postValue(_favoriteRooms.value)
    }

    private suspend fun replaceFavorites(rooms: List<LaundryRoom>, usages: List<LaundryUsage>) {
        favoritesMutex.withLock {
            _favoriteRooms.value?.favoriteRooms?.clear()
            _favoriteRooms.value?.roomsData?.clear()

            _favoriteRooms.value?.favoriteRooms?.addAll(rooms)
            _favoriteRooms.value?.roomsData?.addAll(usages)
        }
    }
    fun getHasPreferences() = hasPreferences
    fun getFavorites(studentLife: StudentLife, bearerToken : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteIdList : MutableList<Int> = mutableListOf()
            val response = studentLife.getLaundryPref2(bearerToken)
            if (response.isSuccessful) {
               val prefs = response.body()!!.rooms
               for (room in prefs!!) {
                   favoriteIdList.add(room)
               }
            } else {
               Log.i("Laundry Test", "fuck")
            }

            val rooms = ArrayList<LaundryRoom>()
            val usages = ArrayList<LaundryUsage>()

            for (roomId in favoriteIdList) {
                var addRoomSuccess = false
                var addUsageSuccess = false
                val addRoom = launch {
                    val roomResponse = studentLife.room2(roomId)
                    addRoomSuccess = if (roomResponse.isSuccessful) {
                        rooms.add(roomResponse.body()!!)
                        true
                    } else {
                        Log.i("Laundry Test", "fuck2")
                        false
                    }
                }
                val addUsage = launch {
                    val usageResponse = studentLife.usage2(roomId)
                    addUsageSuccess = if (usageResponse.isSuccessful) {
                        usages.add(usageResponse.body()!!)
                        true
                    } else {
                        Log.i("Laundry Test", "fuck3")
                        false
                    }
                }

                addRoom.join()
                addUsage.join()

                Log.i("Laundry Test", "pls $addRoomSuccess $addUsageSuccess")

                if (addUsageSuccess && !addRoomSuccess) {
                    rooms.removeLast()
                }
                if (!addUsageSuccess && addRoomSuccess) {
                    usages.removeLast()
                }
            }
            replaceFavorites(rooms, usages)
            updateFavorites()

            hasPreferences = true
        }
    }

    fun test(studentLife: StudentLife) {
        CoroutineScope(Dispatchers.IO).launch {
            val idList : MutableList<Int> = mutableListOf()
            val response = studentLife.laundryRooms2()
            if (response.isSuccessful) {
                val rooms = response.body()!!
                for (room in rooms) {
                    idList.add(room.id!!)
                }
                Log.i("Laundry Test", "${rooms.size}")
            } else {
                Log.i("Laundry Test", "bruh")
            }
        }
    }
}