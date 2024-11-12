package com.pennapps.labs.pennmobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import java.net.URL

class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update Server/Database
        Log.d("FCM Registration", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Notification", "Notification received!")
        val title = message.notification?.title
        val body = message.notification?.body
        val imageUrl = message.notification?.imageUrl

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

        val notificationBuilder =
            NotificationCompat
                .Builder(this, "MAIN_CHANNEL")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        imageUrl?.let {
            val bitmap = BitmapFactory.decodeStream(URL(imageUrl.toString()).openConnection().getInputStream())
            notificationBuilder.setLargeIcon(bitmap)
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        notificationManager.notify(1, notificationBuilder.build())
    }
}
