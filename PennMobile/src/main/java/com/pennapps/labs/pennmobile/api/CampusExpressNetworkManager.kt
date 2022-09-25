package com.pennapps.labs.pennmobile.api

import android.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import java.util.*

class CampusExpressNetworkManager(private var mActivity: MainActivity) {

    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor = sp?.edit()


    fun getAccessToken() : String? {
        val expiresIn = sp.getLong(mActivity.getString(R.string.campus_token_expires_in), 0L)
        if (expiresIn != 0L) {
            val calendar = Calendar.getInstance()
            val expiresAt = Date()
            expiresAt.time = expiresIn
            calendar.time = Date()

            if (calendar.time >= expiresAt) { // if it has expired, refresh access token
                return ""
            }
            return sp.getString(mActivity.getString(R.string.campus_express_token), "")
        } else {
            return ""
        }
    }
}