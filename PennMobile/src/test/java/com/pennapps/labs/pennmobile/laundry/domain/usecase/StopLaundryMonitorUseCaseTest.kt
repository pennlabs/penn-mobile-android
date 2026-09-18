package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.laundry.FakeLaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.FakeLaundryMonitorScheduler
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import org.junit.Assert.assertEquals
import org.junit.Test

class StopLaundryMonitorUseCaseTest {
    private val repo = FakeLaundryMonitorRepo()
    private val scheduler = FakeLaundryMonitorScheduler()
    private val stopMonitoring = StopLaundryMonitorUseCase(repo, scheduler)

    @Test
    fun `clears the stored mode and cancels queued work`() {
        stopMonitoring()

        assertEquals(listOf(MonitorMode.OFF), repo.modesSet)
        assertEquals(1, scheduler.cancelCount)
    }
}
