package com.pennapps.labs.pennmobile.laundry.work

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import java.util.concurrent.TimeUnit

/**
 * Builds the work request for a single availability poll.
 *
 * Kept free of any WorkManager instance so its output can be asserted on in a plain JVM test.
 */
object LaundryPollRequest {
    const val UNIQUE_WORK_NAME = "laundry_availability_monitor"
    const val KEY_MODE = "monitor_mode"
    const val KEY_STARTED_AT = "start_time"

    const val POLL_INTERVAL_MINUTES = 3L
    const val MONITOR_WINDOW_MS = 60 * 60 * 1000L

    fun build(
        mode: MonitorMode,
        startedAtMillis: Long,
    ): OneTimeWorkRequest {
        val inputData =
            Data
                .Builder()
                .putString(KEY_MODE, mode.prefValue)
                .putLong(KEY_STARTED_AT, startedAtMillis)
                .build()

        return OneTimeWorkRequest
            .Builder(LaundryAvailabilityWorker::class.java)
            .setInitialDelay(POLL_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()
    }
}
