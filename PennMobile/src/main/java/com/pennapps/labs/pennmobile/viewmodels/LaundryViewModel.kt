package com.pennapps.labs.pennmobile.viewmodels

import StudentLife
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.classes.LaundryRoomFavorites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LaundryViewModel : ViewModel() {
    private var loadedRooms = false

    // tied together
    private val _favoriteRooms = MutableLiveData<LaundryRoomFavorites>()
    private val _hasPreferences = MutableLiveData(false)
    val hasPreferences: LiveData<Boolean>
        get() = _hasPreferences

    val favoriteRooms : LiveData<LaundryRoomFavorites>
        get() = _favoriteRooms

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

            _favoriteRooms.value?.favoriteRooms?.clear()
            _favoriteRooms.value?.roomsData?.clear()

            for (roomId in favoriteIdList) {
                var addRoomSuccess = false
                var addUsageSuccess = false
                val addRoom = launch {
                    val roomResponse = studentLife.room2(roomId)
                    addRoomSuccess = if (roomResponse.isSuccessful) {
                        _favoriteRooms.value?.favoriteRooms?.add(roomResponse.body()!!)
                        true
                    } else {
                        Log.i("Laundry Test", "fuck2")
                        false
                    }
                }
                val addUsage = launch {
                    val usageResponse = studentLife.usage2(roomId)
                    addUsageSuccess = if (usageResponse.isSuccessful) {
                        _favoriteRooms.value?.roomsData?.add(usageResponse.body()!!)
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
                    _favoriteRooms.value?.roomsData?.removeLast()
                }
                if (!addUsageSuccess && addRoomSuccess) {
                    _favoriteRooms.value?.favoriteRooms?.removeLast()
                }
            }
        }
    }

}