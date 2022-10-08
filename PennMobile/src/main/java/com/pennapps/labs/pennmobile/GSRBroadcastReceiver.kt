package com.pennapps.labs.pennmobile

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class GSRBroadcastReceiver : BroadcastReceiver() {
    //notifications
    private val textTitle = "PennMobile: GSR Reminder"
    private val smallText = "Your study room reservation is soon!"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAGGO", "onReceive: Broadcast received")
        val name = intent.getStringExtra("roomName")
        val notificationID = intent.getIntExtra("notificationID", 0)
        val time = intent.getStringExtra("gsrTime")
        var cutTime = time?.substring(11, 16)
        val hours = cutTime?.substring(0,2)?.let { Integer.parseInt(it) }
        if (hours != null && hours > 12) {
                val time = hours - 12
                cutTime = time.toString() + cutTime?.substring(2)
                //Convert to 12 hr time
        }
        val textContent = "Your reservation for $name at $cutTime is soon!"
        Log.d("TAGGO", "onReceive: $name")
        val clickIntent = Intent(context, MainActivity::class.java)
        val pendingClickIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)
        var builder = context.let {
            NotificationCompat.Builder(it, R.string.channel_id.toString())
                .setSmallIcon(R.drawable.pennmobile_logo_24x24)
                .setContentTitle(textTitle)
                .setContentText(smallText)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(textContent))
                .setContentIntent(pendingClickIntent) //click into app
                .setTimeoutAfter(10 * 60 * 1000) //only visible for 10 minutes
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
        Log.d("TAGGO", "onReceive: builder made")
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            Log.d("Taggo token", "ID NUMBA $notificationID")
            notify(notificationID, builder.build())
        }
        val key = name + time?.substring(0, time?.length - 6)
        if (MainActivity.GSRIntents.containsKey(key) ){
            val pendingIntent = MainActivity.GSRIntents.get(key)
            MainActivity.GSRAlarmManager?.cancel(pendingIntent)
            MainActivity.GSRIntents.remove(key)
            Log.d("GSR Booking taggo haw", "success: " + MainActivity.GSRIntents.keys.toString())
        }
        Log.d("TAGGO", "onReceive: notif displayed")
    }


}
