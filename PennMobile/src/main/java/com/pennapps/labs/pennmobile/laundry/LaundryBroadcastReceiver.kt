package com.pennapps.labs.pennmobile.laundry

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.laundry.notification.AndroidLaundryNotifier

class LaundryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val roomName = intent.getStringExtra(context.getString(R.string.laundry_room_name))
        val machineType = intent.getStringExtra(context.getString(R.string.laundry_machine_type))
        val id = intent.getIntExtra(context.getString(R.string.laundry_machine_id), -1)

        // checks for errors
        if (roomName == null || machineType == null || id == -1) {
            return
        }

        // Constructed directly rather than injected: a BroadcastReceiver would need its own Hilt
        // entry point for a class whose only dependency is the Context onReceive already provides.
        AndroidLaundryNotifier(context).notifyMachineAvailable(roomName, machineType, id)

        // cancel intent after notification/alarm goes off
        val fromIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        fromIntent?.cancel()
    }
}
