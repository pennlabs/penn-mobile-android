package com.pennapps.labs.pennmobile.dining.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import java.util.Calendar

// An AlarmManager for app widget testing (Shortens the update time)
class AppWidgetAlarm(
    private val mContext: Context,
) {
    val alarmId: Int = 0
    val internalMillis: Int = 100000

    fun startAlarm() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, internalMillis)
        val alarmIntent = Intent(mContext, GsrReservationWidget::class.java)
        alarmIntent.setAction(GsrReservationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            internalMillis.toLong(),
            pendingIntent,
        )
    }

    fun stopAlarm() {
        val alarmIntent = Intent(GsrReservationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun alarmUp(): Boolean {
        val alarmIntent = Intent(GsrReservationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent != null
    }
}
