package com.pennapps.labs.pennmobile.laundry.work

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import javax.inject.Inject

class WorkManagerLaundryScheduler
    @Inject
    constructor(
        private val workManager: WorkManager,
    ) : LaundryMonitorScheduler {
        override fun startPolling(
            mode: MonitorMode,
            startedAtMillis: Long,
        ) {
            workManager.enqueueUniqueWork(
                LaundryPollRequest.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                LaundryPollRequest.build(mode, startedAtMillis),
            )
        }

        override suspend fun scheduleNextPoll(
            mode: MonitorMode,
            startedAtMillis: Long,
        ) {
            // APPEND rather than REPLACE: this is called from inside the running poll, and REPLACE
            // would cancel that very worker. OR_REPLACE restarts the chain if it was cancelled or
            // failed. Awaited so the enqueue is durable before the worker returns.
            workManager
                .enqueueUniqueWork(
                    LaundryPollRequest.UNIQUE_WORK_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    LaundryPollRequest.build(mode, startedAtMillis),
                ).await()
        }

        override fun cancelPolling() {
            workManager.cancelUniqueWork(LaundryPollRequest.UNIQUE_WORK_NAME)
        }
    }
