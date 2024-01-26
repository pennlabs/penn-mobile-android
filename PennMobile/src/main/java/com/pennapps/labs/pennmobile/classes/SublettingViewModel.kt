package com.pennapps.labs.pennmobile.classes

import android.app.Activity
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife

class SublettingViewModel (private val activity: Activity, private val studentLife: StudentLife) {

    fun postSublet(mActivity : MainActivity, sublet : Sublet) {

        val context = activity.applicationContext
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)


        OAuth2NetworkManager(mActivity).getAccessToken {

            val bearerToken =
                    "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()


            studentLife.createSublet(bearerToken, sublet)
        }


    }


}