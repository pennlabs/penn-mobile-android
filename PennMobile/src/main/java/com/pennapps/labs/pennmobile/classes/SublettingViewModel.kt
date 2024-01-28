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

class SublettingViewModel (private val activity: Activity, private val studentLife: StudentLife) {

    fun postSublet(mActivity : MainActivity, sublet : Sublet) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.createSublet(bearerToken, sublet, object : ResponseCallback() {
                override fun success(response: Response) {
                    Log.i("Subletting View Model", "Sublet succesfully posted")
                }

                override fun failure(error: RetrofitError) {
                    Log.e(
                            "Subletting View Model", "Error posting sublet " +
                            " $error", error
                    )
                }
            })
        }


    }


}