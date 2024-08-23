package com.pennapps.labs.pennmobile.viewmodels

import StudentLifeRf2
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.classes.LaundryRequest
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomFavorites
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class LaundryViewModel : ViewModel() {
    companion object {
        const val MAX_NUM_ROOMS = 3
    }

    private val laundryRooms: HashMap<String, List<LaundryRoomSimple>> = HashMap()
    private val laundryHalls: ArrayList<String> = ArrayList()

    private val _loadedRooms = MutableLiveData(false)
    val loadedRooms: LiveData<Boolean>
        get() = _loadedRooms

    private val _favoriteRooms = MutableLiveData(LaundryRoomFavorites())

    private val curToggled: MutableSet<Int> = HashSet()

    val favoriteRooms: LiveData<LaundryRoomFavorites>
        get() = _favoriteRooms

    private val favoritesMutex = Mutex()

    private fun updateFavorites() {
        // kinda clown, but rebind so the observer can observe a change
        _favoriteRooms.postValue(_favoriteRooms.value)
    }

    private suspend fun replaceFavorites(
        rooms: List<LaundryRoom>,
        usages: List<LaundryUsage>,
    ) {
        favoritesMutex.withLock {
            _favoriteRooms.value?.favoriteRooms?.clear()
            _favoriteRooms.value?.roomsData?.clear()

            _favoriteRooms.value?.favoriteRooms?.addAll(rooms)
            _favoriteRooms.value?.roomsData?.addAll(usages)
        }
    }

    private suspend fun populateFavorites(
        context: CoroutineContext,
        studentLife: StudentLifeRf2,
        favoriteIdList: List<Int>,
    ) {
        val rooms = ArrayList<LaundryRoom>()
        val usages = ArrayList<LaundryUsage>()

        for (roomId in favoriteIdList) {
            var addRoomSuccess = false
            var addUsageSuccess = false
            val addRoom =
                CoroutineScope(context).launch {
                    try {
                        val roomResponse = studentLife.room(roomId)
                        addRoomSuccess =
                            if (roomResponse.isSuccessful) {
                                val room = roomResponse.body()!!
                                room.id = roomId
                                rooms.add(room)
                                Log.i("Laundry", "Room data fetched")
                                true
                            } else {
                                Log.i("Laundry", "Failed to get room data")
                                false
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            val addUsage =
                CoroutineScope(context).launch {
                    try {
                        val usageResponse = studentLife.usage(roomId)
                        addUsageSuccess =
                            if (usageResponse.isSuccessful) {
                                usages.add(usageResponse.body()!!)
                                true
                            } else {
                                Log.i("Laundry", "Failed to get usage data")
                                false
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            addRoom.join()
            addUsage.join()

            if (addUsageSuccess && !addRoomSuccess) {
                rooms.removeLast()
            }
            if (!addUsageSuccess && addRoomSuccess) {
                usages.removeLast()
            }
        }
        replaceFavorites(rooms, usages)
        updateFavorites()
    }

    fun getFavorites(
        studentLife: StudentLifeRf2,
        bearerToken: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteIdList: MutableList<Int> = mutableListOf()
                val response = studentLife.getLaundryPref(bearerToken)
                if (response.isSuccessful) {
                    val prefs = response.body()!!.rooms
                    for (room in prefs!!) {
                        favoriteIdList.add(room)
                    }
                } else {
                    Log.i("Laundry", "Failed to get preferences")
                }
                populateFavorites(coroutineContext, studentLife, favoriteIdList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getHalls(studentLife: StudentLifeRf2) {
        if (_loadedRooms.value!!) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val roomsResponse = studentLife.laundryRooms()
                if (roomsResponse.isSuccessful) {
                    val rooms = roomsResponse.body()!!
                    laundryRooms.clear()
                    laundryHalls.clear()

                    val numRooms = rooms.size
                    var i = 0
                    // go through all the rooms
                    while (i < numRooms) {
                        // new list for the rooms in the hall
                        var roomList: MutableList<LaundryRoomSimple> = ArrayList()

                        // if hall name already exists, get the list of rooms and add to that
                        var hallName = rooms[i].location ?: ""

                        if (laundryHalls.contains(hallName)) {
                            roomList = laundryRooms[hallName] as MutableList<LaundryRoomSimple>
                            laundryRooms.remove(hallName)
                            laundryHalls.remove(hallName)
                        }

                        while (hallName == rooms[i].location) {
                            roomList.add(rooms[i])

                            i += 1
                            if (i >= rooms.size) {
                                break
                            }
                        }

                        // name formatting for consistency
                        if (hallName == "Lauder College House") {
                            hallName = "Lauder"
                        }

                        // add the hall name to the list
                        laundryHalls.add(hallName)
                        laundryRooms[hallName] = roomList
                    }
                } else {
                    Log.i("Laundry", "Failed to get laundry rooms")
                }

                _loadedRooms.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun existsDiff(): Boolean {
        var diff = false
        runBlocking {
            favoritesMutex.withLock {
                if (_favoriteRooms.value == null) {
                    return@runBlocking
                }
                if (_favoriteRooms.value!!.favoriteRooms.size != curToggled.size) {
                    diff = true
                    return@runBlocking
                }
                for (room in _favoriteRooms.value!!.favoriteRooms) {
                    if (!curToggled.contains(room.id)) {
                        diff = true
                        return@runBlocking
                    }
                }
            }
        }
        return diff
    }

    private suspend fun sendPreferences(
        studentLife: StudentLifeRf2,
        bearerToken: String,
        favoriteIdList: List<Int>,
    ) {
        try {
            val laundryRequest = LaundryRequest(favoriteIdList)
            val response = studentLife.sendLaundryPref(bearerToken, laundryRequest)
            if (response.isSuccessful) {
                Log.i("Laundry Preferences", "Successfully updated preferences")
            } else {
                Log.i("Laundry Preferences", "Error updating preferences")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFavoritesFromToggled(
        studentLife: StudentLifeRf2,
        bearerToken: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // make a copy of the set
            val favoriteIdList = curToggled.toList()
            populateFavorites(coroutineContext, studentLife, favoriteIdList)

            // send
            sendPreferences(studentLife, bearerToken, favoriteIdList)
        }
    }

    fun setToggled() {
        curToggled.clear()
        runBlocking {
            favoritesMutex.withLock {
                for (room in _favoriteRooms.value!!.favoriteRooms) {
                    curToggled.add(room.id)
                }
            }
        }
    }

    // returns true if there is a change state
    fun toggle(roomId: Int): Boolean {
        val origState = curToggled.size >= MAX_NUM_ROOMS
        if (curToggled.contains(roomId)) {
            curToggled.remove(roomId)
        } else {
            curToggled.add(roomId)
        }
        return (curToggled.size >= MAX_NUM_ROOMS).xor(origState)
    }

    fun getGroupCount(): Int {
        return laundryHalls.size
    }

    fun getChildrenCount(i: Int): Int {
        return laundryRooms[laundryHalls[i]]!!.size
    }

    fun getGroup(i: Int): Any {
        return laundryHalls[i]
    }

    fun getChild(
        i: Int,
        i1: Int,
    ): Any {
        return laundryRooms[laundryHalls[i]]!![i1]
    }

    fun getRooms(hallName: String): List<LaundryRoomSimple>? {
        return laundryRooms[hallName]
    }

    fun isChecked(roomId: Int): Boolean {
        return curToggled.contains(roomId)
    }

    fun isFull() = (curToggled.size >= MAX_NUM_ROOMS)
}
