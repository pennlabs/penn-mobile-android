package com.pennapps.labs.pennmobile.dining.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        val alarmIntent = Intent(mContext, DiningHallWidget::class.java)
        alarmIntent.setAction(DiningHallWidget.ACTION_AUTO_UPDATE)
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
        val alarmIntent = Intent(DiningHallWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun alarmUp(): Boolean {
        val alarmIntent = Intent(DiningHallWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null) {
            return true
        }
        return false
    }
}
