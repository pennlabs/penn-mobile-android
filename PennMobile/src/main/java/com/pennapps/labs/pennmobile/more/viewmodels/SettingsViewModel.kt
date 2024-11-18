package com.pennapps.labs.pennmobile.more.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.NotificationAPI

// Currently only implemented the notification logic, other network logistics to be implemented

class SettingsViewModel : ViewModel() {
    suspend fun deleteTokenResponse(
        mNotificationAPI: NotificationAPI,
        bearerToken: String,
        notifToken: String,
    ) {
        try {
            val response = mNotificationAPI.deleteNotificationToken(bearerToken, notifToken)
            if (response.isSuccessful) {
                Log.i("Notification Token", "Successfully deleted token")
            } else {
                Log.i("Notification Token", "Error deleting token: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
