package com.pennapps.labs.pennmobile.laundry

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import java.util.concurrent.TimeUnit

class LaundryAvailabilityWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val mode = inputData.getString("monitor_mode") ?: return Result.failure()
        val startTime = inputData.getLong("start_time", 0L)
        val bearerToken = inputData.getString("bearer_token") ?: return Result.failure()

        // Stop after 1 hour
        if (System.currentTimeMillis() - startTime >= ONE_HOUR_MS) {
            resetMonitorMode()
            return Result.success()
        }

        val isAvailable = checkAvailability(bearerToken, mode)

        if (isAvailable) {
            sendNotification(mode)
            resetMonitorMode()
            return Result.success()
        }

        // Re enqueue self with delay preserving the original start_time
        val nextInput =
            Data
                .Builder()
                .putString("monitor_mode", mode)
                .putLong("start_time", startTime)
                .putString("bearer_token", bearerToken)
                .build()

        val nextWork =
            OneTimeWorkRequest
                .Builder(LaundryAvailabilityWorker::class.java)
                .setInitialDelay(POLL_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .setInputData(nextInput)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "laundry_availability_monitor",
            ExistingWorkPolicy.REPLACE,
            nextWork,
        )

        return Result.success()
    }

    private suspend fun checkAvailability(
        bearerToken: String,
        mode: String,
    ): Boolean {
        val studentLife = MainActivity.studentLifeInstance
        val targetType = if (mode == "WASHERS") "washer" else "dryer"

        return try {
            val prefResponse = studentLife.getLaundryPref(bearerToken)
            if (!prefResponse.isSuccessful) return false
            val roomIds = prefResponse.body()?.rooms ?: return false

            for (roomId in roomIds) {
                try {
                    val roomResponse = studentLife.room(roomId)
                    if (roomResponse.isSuccessful) {
                        val machines = roomResponse.body()?.machines?.machineDetailList ?: continue
                        val hasAvailable =
                            machines.any {
                                it.type == targetType &&
                                    it.timeRemaining == 0 &&
                                    it.status != "Out of order" &&
                                    it.status != "Not online"
                            }
                        if (hasAvailable) return true
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun resetMonitorMode() {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putString("laundry_monitor_mode", "OFF")
            .apply()
    }

    private fun sendNotification(mode: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "pennmobile_laundry_alarm"
        val channel =
            NotificationChannel(channelId, "Laundry Alarm", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Alarm for laundry machine availability"
                enableLights(true)
                lightColor = ContextCompat.getColor(context, R.color.color_primary)
            }
        notificationManager.createNotificationChannel(channel)
        // notif builder
        val machineType = if (mode == "WASHERS") "washer" else "dryer"
        val notification =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_bottom_nav_laundry_grey)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("A $machineType in your favorite rooms is available!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.color_primary))
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val ONE_HOUR_MS = 60 * 60 * 1000L
        private const val POLL_INTERVAL_MINUTES = 3L
        private const val NOTIFICATION_ID = 1001
    }
}
