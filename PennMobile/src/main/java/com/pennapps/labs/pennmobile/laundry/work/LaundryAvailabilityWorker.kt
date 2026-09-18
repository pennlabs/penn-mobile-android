package com.pennapps.labs.pennmobile.laundry.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.PollOutcome
import com.pennapps.labs.pennmobile.laundry.domain.usecase.PollLaundryAvailabilityUseCase
import com.pennapps.labs.pennmobile.laundry.domain.usecase.StopLaundryMonitorUseCase
import com.pennapps.labs.pennmobile.laundry.notification.LaundryNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Runs one availability poll and translates the result into side effects.
 *
 * Deliberately thin: everything worth testing lives in [PollLaundryAvailabilityUseCase], which runs
 * on a plain JVM. A CoroutineWorker cannot be constructed outside an Android runtime.
 */
@HiltWorker
class LaundryAvailabilityWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val pollAvailability: PollLaundryAvailabilityUseCase,
        private val stopMonitoring: StopLaundryMonitorUseCase,
        private val scheduler: LaundryMonitorScheduler,
        private val notifier: LaundryNotifier,
    ) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result {
            val mode = MonitorMode.fromPrefValue(inputData.getString(LaundryPollRequest.KEY_MODE))
            val startedAt = inputData.getLong(LaundryPollRequest.KEY_STARTED_AT, 0L)

            // Every path returns success: work appended to this one is marked failed otherwise.
            return when (val outcome = pollAvailability(mode, startedAt, System.currentTimeMillis())) {
                PollOutcome.Available -> {
                    notifier.notifyMonitoredMachineAvailable(mode.machineLabel)
                    stopMonitoring()
                    Result.success()
                }

                PollOutcome.StillBusy -> {
                    scheduler.scheduleNextPoll(mode, startedAt)
                    Result.success()
                }

                PollOutcome.Expired -> {
                    stopMonitoring()
                    Result.success()
                }

                is PollOutcome.Failed -> {
                    if (runAttemptCount < MAX_ATTEMPTS) {
                        Result.retry()
                    } else {
                        stopMonitoring()
                        Result.success()
                    }
                }
            }
        }

        companion object {
            private const val MAX_ATTEMPTS = 3
        }
    }
