package com.pennapps.labs.pennmobile.laundry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRequest
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoomFavorites
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.laundry.classes.LaundryUsage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val _loadedFavorites = MutableLiveData(false)
    val loadedFavorites: LiveData<Boolean>
        get() = _loadedFavorites

    val isDataReady: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(_loadedRooms) { value = (_loadedRooms.value == true && _loadedFavorites.value == true) }
        addSource(_loadedFavorites) { value = (_loadedRooms.value == true && _loadedFavorites.value == true) }
    }

    private val _favoriteRooms = MutableLiveData(LaundryRoomFavorites())

    private val curToggled: MutableSet<Int> = HashSet()


    private var savedFavoriteIds: Set<Int> = emptySet()


    @Volatile
    private var pendingPrefWrites: Int = 0

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
        studentLife: StudentLife,
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
                usages.removeAt(usages.lastIndex)
            }
            if (!addUsageSuccess && addRoomSuccess) {
                rooms.removeAt(rooms.lastIndex)
            }
        }
        replaceFavorites(rooms, usages)
        updateFavorites()
    }

    fun getFavorites(
        studentLife: StudentLife,
        bearerToken: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteIdList: MutableList<Int> = mutableListOf()
                val response = studentLife.getLaundryPref(bearerToken)
                if (response.isSuccessful) {
                    val prefs = response.body()?.rooms ?: emptyList()
                    for (room in prefs) {
                        favoriteIdList.add(room)
                    }
                } else {
                    Log.i("Laundry", "Failed to get preferences")
                }

                if (pendingPrefWrites == 0) {
                    populateFavorites(coroutineContext, studentLife, favoriteIdList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loadedFavorites.postValue(true)
            }
        }
    }

    fun getHalls(studentLife: StudentLife) {
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

    fun existsDiff(): Boolean = curToggled != savedFavoriteIds

    private suspend fun sendPreferences(
        studentLife: StudentLife,
        bearerToken: String,
        favoriteIdList: List<Int>,
    ): Boolean {
        try {
            val laundryRequest = LaundryRequest(favoriteIdList)
            val response = studentLife.sendLaundryPref(bearerToken, laundryRequest)
            if (response.isSuccessful) {
                Log.i("Laundry Preferences", "Successfully updated preferences")
                return true
            }
            Log.i("Laundry Preferences", "Error updating preferences")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun setFavoritesFromToggled(
        studentLife: StudentLife,
        bearerToken: String,
    ) {
        // make a copy of the set
        val favoriteIdList = curToggled.toList()

        // Claimed here rather than inside the coroutine so that a getFavorites() racing us
        // from the Laundry page sees the pending write even if it runs first.
        pendingPrefWrites += 1

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (sendPreferences(studentLife, bearerToken, favoriteIdList)) {
                    savedFavoriteIds = favoriteIdList.toSet()
                    populateFavorites(coroutineContext, studentLife, favoriteIdList)
                }
                // selection that was never persisted.
            } finally {
                pendingPrefWrites -= 1
            }
        }
    }

    fun setToggled() {
        curToggled.clear()
        for (room in _favoriteRooms.value?.favoriteRooms.orEmpty()) {
            curToggled.add(room.id)
        }
        savedFavoriteIds = curToggled.toSet()
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

    fun getGroupCount(): Int = laundryHalls.size

    fun getChildrenCount(i: Int): Int = laundryRooms[laundryHalls[i]]!!.size

    fun getGroup(i: Int): Any = laundryHalls[i]

    fun getChild(
        i: Int,
        i1: Int,
    ): Any = laundryRooms[laundryHalls[i]]!![i1]

    fun getRooms(hallName: String): List<LaundryRoomSimple>? = laundryRooms[hallName]

    fun isChecked(roomId: Int): Boolean = curToggled.contains(roomId)

    fun isFull() = (curToggled.size >= MAX_NUM_ROOMS)
}
