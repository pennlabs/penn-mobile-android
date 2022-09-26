package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class GSRBroadcastReceiver : BroadcastReceiver() {
    //notifications
    private val textTitle = "PennMobile: GSR Reminder"
    private val notificationId = 0

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAGGO", "onReceive: Broadcast received")
        val name = intent.getStringExtra("roomName")
        val textContent = "Your reservation for $name is in 10 minutes!"
        Log.d("TAGGO", "onReceive: $name")
        var builder = context.let {
            NotificationCompat.Builder(it, R.string.channel_id.toString())
                .setSmallIcon(R.drawable.pennmobile_logo_24x24)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
        Log.d("TAGGO", "onReceive: builder made")
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
        Log.d("TAGGO", "onReceive: notif displayed")
    }


}
