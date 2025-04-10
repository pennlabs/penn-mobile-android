package com.pennapps.labs.pennmobile.fitness

import StudentLife
import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.fitness.classes.FitnessAdapterDataModel
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRequest
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoom
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FitnessPreferenceViewModel(
    private val studentLife: StudentLife,
    private val roomList: List<FitnessRoom>,
) : FitnessAdapterDataModel {
    private val roomTot = roomList.size

    // hashset of the favorite room ids
    private val favoriteRooms: HashSet<Int> = hashSetOf()
    private val prevFavoriteRooms: HashSet<Int> = hashSetOf()

    // hashmap that maps position --> position in array
    private val positionMap: Array<Int> = (0 until roomTot).toList().toTypedArray()

    override fun flipState(roomId: Int): Boolean {
        if (favoriteRooms.contains(roomId)) {
            favoriteRooms.remove(roomId)
            return false
        }
        favoriteRooms.add(roomId)
        return true
    }

    override fun getNumber(isFavorite: Boolean): Int {
        if (isFavorite) {
            return favoriteRooms.size
        }
        return roomTot - favoriteRooms.size
    }

    override fun getRoom(
        isFavorite: Boolean,
        position: Int,
    ): FitnessRoom {
        if (isFavorite) {
            return roomList[positionMap[position]]
        }
        return roomList[positionMap[position + favoriteRooms.size]]
    }

    override fun getTot(): Int = roomTot

    override fun getRoomAll(roomId: Int): FitnessRoom = roomList[roomId]

    override fun isFavorite(roomId: Int): Boolean = favoriteRooms.contains(roomId)

    fun clearFavorites() {
        favoriteRooms.clear()
    }

    fun addId(roomId: Int) {
        favoriteRooms.add(roomId)
    }

    fun updatePositionMap() {
        val numFavorites = favoriteRooms.size
        var curFavIndex = 0
        var curOtherIndex = 0
        for (i in 0 until roomTot) {
            if (favoriteRooms.contains(roomList[i].roomId)) {
                positionMap[curFavIndex++] = i
            } else {
                positionMap[numFavorites + curOtherIndex++] = i
            }
        }
    }

    fun savePreferences() {
        prevFavoriteRooms.clear()
        prevFavoriteRooms.addAll(favoriteRooms)
    }

    fun restorePreferences() {
        favoriteRooms.clear()
        favoriteRooms.addAll(prevFavoriteRooms)
    }

    fun updateRemotePreferences(mActivity: MainActivity) {
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val context = mActivity.applicationContext

        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            // Global since this is not a real view model. (yes, I was naive and dumb)
            // Also, this is only used for sending preferences (not critical) and there is no actual
            // logic being executed on callback beyond logging
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response =
                        studentLife.sendFitnessPref(
                            bearerToken,
                            FitnessRequest(ArrayList(favoriteRooms)),
                        )

                    if (response.isSuccessful) {
                        Log.i("Fitness Preference View Model", "fitness preferences saved")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                        Log.e(
                            "Fitness Preference View Model",
                            "Error saving fitness " +
                                "preferences: $errorBody",
                            Exception(errorBody),
                        )
                    }
                } catch (e: Exception) {
                    Log.e("FitnessPreference", "Network call failed", e)
                }
            }
        }
    }
}
