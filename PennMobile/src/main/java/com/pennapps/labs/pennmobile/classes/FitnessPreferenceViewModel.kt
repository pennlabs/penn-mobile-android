package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response

class FitnessPreferenceViewModel(private val activity: Activity,
    private val studentLife: StudentLife, private val roomList: List<FitnessRoom>) : FitnessAdapterDataModel {

    private val roomTot = roomList.size

    // hashset of the favorite room ids
    private val favoriteRooms : HashSet<Int>  = hashSetOf()
    private val prevFavoriteRooms :  HashSet<Int> = hashSetOf()

    // hashmap that maps position --> position in array
    private val positionMap : Array<Int> = (0 until roomTot).toList().toTypedArray()

    override fun flipState(roomId: Int) : Boolean {
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

    override fun getRoom(isFavorite: Boolean, position: Int): FitnessRoom {
        if (isFavorite) {
            return roomList[positionMap[position]]
        }
        return roomList[positionMap[position + favoriteRooms.size]]
    }

    override fun getTot(): Int {
        return roomTot
    }

    override fun getRoomAll(roomId: Int): FitnessRoom {
        return roomList[roomId]
    }

    override fun isFavorite(roomId: Int): Boolean {
        return favoriteRooms.contains(roomId)
    }

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

    fun updateRemotePreferences(mActivity : MainActivity) {
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val context = activity.applicationContext

        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.sendFitnessPref(bearerToken, FitnessRequest(ArrayList(favoriteRooms)),
                object : ResponseCallback() {
                    override fun success(response: Response) {
                        Log.i("Fitness Preference View Model", "fitness preferences saved")
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e(
                            "Fitness Preference View Model", "Error saving fitness " +
                                    "preferences: $error", error
                        )
                    }
                })
        }
    }

}