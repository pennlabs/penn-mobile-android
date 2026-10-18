package com.pennapps.labs.pennmobile.laundry.work

import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode

/**
 * Background polling for laundry availability.
 *
 * This exists as an interface so the monitoring logic can be unit tested on the JVM: WorkManager
 * cannot be instantiated outside an Android runtime, but a fake of this can.
 */
interface LaundryMonitorScheduler {
    /** Begins a fresh monitoring run, replacing any run already in progress. */
    fun startPolling(
        mode: MonitorMode,
        startedAtMillis: Long,
    )

    /** Queues the next poll of an in-progress run, preserving its original start time. */
    suspend fun scheduleNextPoll(
        mode: MonitorMode,
        startedAtMillis: Long,
    )

    fun cancelPolling()
}
