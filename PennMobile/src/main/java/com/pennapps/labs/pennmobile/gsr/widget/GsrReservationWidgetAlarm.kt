package com.pennapps.labs.pennmobile.gsr.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class GsrReservationWidgetAlarm(
    private val mContext: Context,
) {
    private val alarmId: Int = 0
    // val internalMillis: Int = 60000

    fun startAlarm() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, AlarmManager.INTERVAL_HALF_HOUR.toInt())
        val alarmIntent = Intent(mContext, GsrReservationWidget::class.java)
        alarmIntent.setAction(GsrReservationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR, // AlarmManager.INTERVAL_HALF_HOUR
            pendingIntent,
        )
    }

    fun stopAlarm() {
        val alarmIntent = Intent(GsrReservationWidget.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

//    fun alarmUp(): Boolean {
//        val alarmIntent = Intent(DiningHallWidget.ACTION_AUTO_UPDATE)
//        val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
//        if (pendingIntent != null) {
//            return true
//        }
//        return false
//    }
}
