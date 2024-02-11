package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import retrofit.ResponseCallback
import com.pennapps.labs.pennmobile.classes.Sublet
import kotlinx.android.synthetic.main.loading_panel.loadingPanel
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.Callback

class SublettingViewModel (private val activity: Activity, private val studentLife: StudentLife) {

    fun postSublet(mActivity : MainActivity, sublet : Sublet) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.createSublet(bearerToken, sublet,
                    object : Callback<Sublet> {
                        override fun success(t: Sublet?, response: Response?) {
                            Log.i("Subletting View Model", "sublet posted")
                        }

                        override fun failure(error: RetrofitError?) {
                            Log.e("Subletting View Model", "Error posting sublet " +
                                    "$error", error
                            )
                            Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()


                        }

                    })


            studentLife.getPostedSublets(bearerToken).subscribe({ sublets ->
                mActivity.runOnUiThread {
                    //return sublets

                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                        "Posted Sublet Fragment",
                        "Could not load Posted Sublets",
                        throwable
                    )
                }
            })
        }


    }


}