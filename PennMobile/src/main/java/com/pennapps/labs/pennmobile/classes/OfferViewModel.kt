package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife

class OfferViewModel (private val activity: Activity, private val studentLife: StudentLife) {

    var offersList = MutableLiveData<ArrayList<Offer>>()

    fun getOffers(mActivity: MainActivity, id: Int) {
        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            /* studentLife.getSubletOffers(bearerToken, id).subscribe({ offers ->
                mActivity.runOnUiThread {
                    offersList.value = offers as ArrayList<Offer>
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e(
                        "Sublet Candidates Fragment",
                        "Could not load Offers",
                        throwable
                    )
                }
            }) */
        }
    }

    fun getOffer(position : Int) : Offer {
        return offersList.value?.get(position) ?: Offer() // Provide a default value if needed
    }
}