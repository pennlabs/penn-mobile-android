package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.concurrent.CountDownLatch

class SublesseeViewModel(private val activity: Activity, private val studentLife: StudentLife) {

    private var sublettingList = ArrayList<Sublet>()

    fun getSublettingList(): ArrayList<Sublet> {
        return sublettingList
    }

    fun listSublets(mActivity: MainActivity) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        //var sublettingList = ArrayList<Sublet>()
        //var fitnessRoomsList = ArrayList<FitnessRoom>()

        //for bearer token- not necessary when listing the sublets
        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()

            /* studentLife.fitnessRooms
                    .subscribe { fitnessRooms ->
                        for (fitnessRoom in fitnessRooms) {
                            Log.i("fitness room", "${fitnessRoom.roomId}")
                            fitnessRoomsList.add(fitnessRoom)
                        }
                    } */
            studentLife.getSublets(bearerToken)
                    .subscribe { sublets ->
                        for (sublet in sublets) {
                            Log.i("Sublet${sublet.id}", "${sublet.title}")
                            sublettingList.add(sublet)
                        }
                        Log.i("Sublets not initializing", sublets.size.toString())
                        Log.i("Sublets not initializing", sublettingList.size.toString())
                        //sublettingList = sublets as ArrayList<Sublet>
                    }
        }
        Log.i("sublets", sublettingList.size.toString())
    }
}