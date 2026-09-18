package com.pennapps.labs.pennmobile.laundry

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.laundry.notification.AndroidLaundryNotifier
import java.util.concurrent.TimeUnit

class LaundryAvailabilityWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val mode = inputData.getString("monitor_mode") ?: return Result.failure()
        val startTime = inputData.getLong("start_time", 0L)

        // Read the token fresh from SharedPreferences
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val bearerToken = "Bearer " + (sharedPrefs.getString(context.getString(R.string.access_token), "") ?: "")

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

        // re enqueue self with delay preserving the original start_time
        val nextInput =
            Data
                .Builder()
                .putString("monitor_mode", mode)
                .putLong("start_time", startTime)
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

        return try {
            val prefResponse = studentLife.getLaundryPref(bearerToken)
            if (!prefResponse.isSuccessful) return false
            val roomIds = prefResponse.body()?.rooms ?: return false

            for (roomId in roomIds) {
                try {
                    val roomResponse = studentLife.room(roomId)
                    if (roomResponse.isSuccessful) {
                        val roomMachines = roomResponse.body()?.machines ?: continue
                        // use the server computed open count
                        val machineList = if (mode == "WASHERS") roomMachines.washers else roomMachines.dryers
                        if ((machineList?.open ?: 0) > 0) return true
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
        val machineLabel = if (mode == "WASHERS") "washer" else "dryer"
        AndroidLaundryNotifier(context).notifyMonitoredMachineAvailable(machineLabel)
    }

    companion object {
        private const val ONE_HOUR_MS = 60 * 60 * 1000L
        private const val POLL_INTERVAL_MINUTES = 3L
    }
}
