package com.pennapps.labs.pennmobile.laundry.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidLaundryNotifier
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LaundryNotifier {
        override fun notifyMachineAvailable(
            roomName: String,
            machineType: String,
            machineId: Int,
        ) = post(
            notificationId = machineId + 1,
            text = context.getString(R.string.laundry_notification_available, machineType, roomName),
        )

        override fun notifyMonitoredMachineAvailable(machineLabel: String) =
            post(
                notificationId = MONITOR_NOTIFICATION_ID,
                text = context.getString(R.string.laundry_notification_monitor_available, machineLabel),
            )

        private fun post(
            notificationId: Int,
            text: String,
        ) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(buildChannel())

            val laundryIntent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            val contentIntent =
                PendingIntent.getActivity(
                    context,
                    notificationId,
                    laundryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_bottom_nav_laundry_grey)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .build()

            notificationManager.notify(notificationId, notification)
        }

        private fun buildChannel() =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = ContextCompat.getColor(context, R.color.color_primary)
            }

        companion object {
            private const val CHANNEL_ID = "pennmobile_laundry_alarm"
            private const val CHANNEL_NAME = "Laundry Alarm"
            private const val CHANNEL_DESCRIPTION = "Alarm for laundry machine availability"

            // Per-machine alarms use machineId + 1, so this sits above any plausible machine id.
            private const val MONITOR_NOTIFICATION_ID = 1001
        }
    }
