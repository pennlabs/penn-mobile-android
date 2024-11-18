package com.pennapps.labs.pennmobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R

class PushNotificationService : FirebaseMessagingService() {
    private lateinit var mSharedPrefs: SharedPreferences

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update Server/Database
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        with(mSharedPrefs.edit()) {
            putString("Notification Token", token)
            apply()
        }

        Log.d("FCM Registration", "Stored Notification token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("Notification Received", "Notification received!")
        val title = message.notification?.title
        val body = message.notification?.body

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(
                "MAIN_CHANNEL",
                "Main Channel",
                NotificationManager.IMPORTANCE_HIGH,
            )
        notificationManager.createNotificationChannel(notificationChannel)

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.apply {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
            flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        val bitMap = BitmapFactory.decodeResource(this.resources, R.drawable.ic_icon)

        val notificationBuilder =
            NotificationCompat
                .Builder(this, "MAIN_CHANNEL")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(bitMap)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.penn_red))

        notificationManager.notify(1, notificationBuilder.build())
    }
}
