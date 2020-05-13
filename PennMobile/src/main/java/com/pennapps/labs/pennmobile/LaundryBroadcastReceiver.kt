package com.pennapps.labs.pennmobile

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class LaundryBroadcastReceiver : BroadcastReceiver() {

    private var notificationID = 0

    override fun onReceive(context: Context, intent: Intent) {
        val roomName = intent.getStringExtra(context.resources.getString(R.string.laundry_room_name))
        val machineType = intent.getStringExtra(context.resources.getString(R.string.laundry_machine_type))
        val id = intent.getIntExtra(context.resources.getString(R.string.laundry_machine_id), -1)

        // checks for errors
        if (roomName == null || machineType == null || id == -1) {
            return
        }
        notificationID = id + 1
        val builder = StringBuilder()
        builder.append("A ").append(machineType).append(" in ").append(roomName).append(" is available!")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // build notification
        val mBuilder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Laundry Alarm"
            val channelId = "pennmobile_laundry_alarm"
            val description = "Alarm for laundry machine availability"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = ContextCompat.getColor(context, R.color.color_primary)
            notificationManager.createNotificationChannel(channel)
            mBuilder = NotificationCompat.Builder(context, channel.id)
                    .setSmallIcon(R.drawable.ic_bottom_nav_laundry_grey)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(builder)
        } else {
            val channelId = "pennmobile_laundry_alarm"
            mBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_bottom_nav_laundry_grey)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(builder)
        }
        mBuilder.setAutoCancel(true)
        mBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.color = ContextCompat.getColor(context, R.color.color_primary)
        }

        // intent to go to main activity
        val laundryIntent = Intent(context, MainActivity::class.java)
        laundryIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val notifyIntent = PendingIntent.getActivity(context, notificationID, laundryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(notifyIntent)
        notificationManager.notify(notificationID, mBuilder.build())

        // cancel intent after notification/alarm goes off
        val fromIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE)
        fromIntent?.cancel()
    }
}