package com.pennapps.labs.pennmobile.api.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.NotificationAPI

// Currently only include logic for notifications, would add more network handling afterwards (TBD)

class LoginWebviewViewmodel : ViewModel() {
    suspend fun sendToken(
        mNotificationAPI: NotificationAPI,
        notGuest: Boolean,
        bearerToken: String,
        notifToken: String,
    ) {
        try {
            if (notGuest) {
                val response = mNotificationAPI.sendNotificationToken(bearerToken, notifToken)
                if (response.isSuccessful) {
                    Log.i("Notification Token", "Successfully updated token")
                } else {
                    Log.i("Notification Token", "Error updating token: ${response.code()} ${response.message()}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
