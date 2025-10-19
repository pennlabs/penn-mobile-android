package com.pennapps.labs.pennmobile.gsr.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class GsrReservationWidgetAlarm(private val context: Context) {
    private val alarmId = System.currentTimeMillis().toInt()
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var isActive = false

    fun startAlarm() {
        if (isActive) return

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, GsrReservationWidget::class.java).apply {
            action = GsrReservationWidget.ACTION_AUTO_UPDATE
        }

        pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager?.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_HOUR,
            60000, // AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent!!
        )
        isActive = true
    }

    fun stopAlarm() {
        if (!isActive) return

        try {
            alarmManager?.cancel(pendingIntent!!)
            pendingIntent?.cancel()
        } catch (e: Exception) {
            Log.e("GsrAlarm", "Error stopping alarm", e)
        } finally {
            pendingIntent = null
            alarmManager = null
            isActive = false
        }
    }
}