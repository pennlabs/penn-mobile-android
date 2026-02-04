package com.pennapps.labs.pennmobile.api

import android.content.Context
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.R
import java.util.Calendar
import java.util.Date

class CampusExpressNetworkManager(
    private var context: Context,
) {
    private val sp = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sp?.edit()

    fun getAccessToken(): String? {
        val expiresIn = sp.getLong(context.getString(R.string.campus_token_expires_in), 0L)
        if (expiresIn != 0L) {
            val calendar = Calendar.getInstance()
            val expiresAt = Date()
            expiresAt.time = expiresIn
            calendar.time = Date()

            if (calendar.time >= expiresAt) { // if it has expired, refresh access token
                return ""
            }
            return sp.getString(context.getString(R.string.campus_express_token), "")
        } else {
            return ""
        }
    }
}
