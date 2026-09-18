package com.pennapps.labs.pennmobile.laundry

import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.work.LaundryMonitorScheduler

class FakeLaundryMonitorScheduler : LaundryMonitorScheduler {
    val started = mutableListOf<Pair<MonitorMode, Long>>()
    val nextPolls = mutableListOf<Pair<MonitorMode, Long>>()
    var cancelCount = 0
        private set

    override fun startPolling(
        mode: MonitorMode,
        startedAtMillis: Long,
    ) {
        started += mode to startedAtMillis
    }

    override suspend fun scheduleNextPoll(
        mode: MonitorMode,
        startedAtMillis: Long,
    ) {
        nextPolls += mode to startedAtMillis
    }

    override fun cancelPolling() {
        cancelCount++
    }
}
