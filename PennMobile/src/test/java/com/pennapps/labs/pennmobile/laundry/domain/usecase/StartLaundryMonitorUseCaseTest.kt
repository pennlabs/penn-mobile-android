package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.FakeLaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.FakeLaundryMonitorScheduler
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.StartOutcome
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StartLaundryMonitorUseCaseTest {
    private val startedAt = 5_000L

    private class Fixture(
        availability: Result<Boolean>,
    ) {
        val repo = FakeLaundryMonitorRepo(availability)
        val scheduler = FakeLaundryMonitorScheduler()
        val start = StartLaundryMonitorUseCase(repo, scheduler, StopLaundryMonitorUseCase(repo, scheduler))
    }

    @Test
    fun `selecting OFF cancels without hitting the network`() =
        runTest {
            val fixture = Fixture(Result.Success(false))

            assertEquals(StartOutcome.Stopped, fixture.start(MonitorMode.OFF, startedAt))
            assertEquals(0, fixture.repo.availabilityChecks)
            assertEquals(1, fixture.scheduler.cancelCount)
            assertTrue(fixture.scheduler.started.isEmpty())
        }

    @Test
    fun `an already free machine does not schedule any polling`() =
        runTest {
            val fixture = Fixture(Result.Success(true))

            assertEquals(StartOutcome.AlreadyAvailable(MonitorMode.WASHERS), fixture.start(MonitorMode.WASHERS, startedAt))
            assertTrue(fixture.scheduler.started.isEmpty())
        }

    @Test
    fun `a busy machine starts polling once with the given start time`() =
        runTest {
            val fixture = Fixture(Result.Success(false))

            assertEquals(StartOutcome.Monitoring(MonitorMode.DRYERS), fixture.start(MonitorMode.DRYERS, startedAt))
            assertEquals(listOf(MonitorMode.DRYERS to startedAt), fixture.scheduler.started)
            assertEquals(MonitorMode.DRYERS, fixture.repo.monitorMode.value)
        }

    /** Fails closed: never tell the user we are monitoring when nothing was scheduled. */
    @Test
    fun `a failed check stops instead of pretending to monitor`() =
        runTest {
            val fixture = Fixture(Result.Error("You are offline."))

            assertEquals(StartOutcome.Failed("You are offline."), fixture.start(MonitorMode.WASHERS, startedAt))
            assertTrue(fixture.scheduler.started.isEmpty())
            assertEquals(1, fixture.scheduler.cancelCount)
            assertEquals(MonitorMode.OFF, fixture.repo.monitorMode.value)
        }
}
