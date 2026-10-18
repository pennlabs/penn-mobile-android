package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.FakeLaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.PollOutcome
import com.pennapps.labs.pennmobile.laundry.work.LaundryPollRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PollLaundryAvailabilityUseCaseTest {
    private val startedAt = 1_000_000L

    private fun useCase(availability: Result<Boolean>) = FakeLaundryMonitorRepo(availability)

    @Test
    fun `reports available when a machine is free`() =
        runTest {
            val repo = useCase(Result.Success(true))
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.WASHERS, startedAt, startedAt + 1)

            assertEquals(PollOutcome.Available, outcome)
        }

    @Test
    fun `reports still busy inside the window when nothing is free`() =
        runTest {
            val repo = useCase(Result.Success(false))
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.DRYERS, startedAt, startedAt + 1)

            assertEquals(PollOutcome.StillBusy, outcome)
        }

    @Test
    fun `expires exactly at the end of the monitoring window`() =
        runTest {
            val repo = useCase(Result.Success(false))
            val now = startedAt + LaundryPollRequest.MONITOR_WINDOW_MS
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.WASHERS, startedAt, now)

            assertEquals(PollOutcome.Expired, outcome)
        }

    @Test
    fun `still polls one millisecond before the window closes`() =
        runTest {
            val repo = useCase(Result.Success(false))
            val now = startedAt + LaundryPollRequest.MONITOR_WINDOW_MS - 1
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.WASHERS, startedAt, now)

            assertEquals(PollOutcome.StillBusy, outcome)
        }

    /**
     * Regression guard: before the repo returned a Result, a 401 from an expired token was caught
     * and turned into false, so the monitor silently reported "nothing available" for a full hour.
     */
    @Test
    fun `a failed check is reported as failed, not as still busy`() =
        runTest {
            val repo = useCase(Result.Error("Log in to monitor laundry machines."))
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.WASHERS, startedAt, startedAt + 1)

            assertEquals(PollOutcome.Failed("Log in to monitor laundry machines."), outcome)
        }

    @Test
    fun `an expired run does not hit the network`() =
        runTest {
            val repo = useCase(Result.Success(false))
            val now = startedAt + LaundryPollRequest.MONITOR_WINDOW_MS
            PollLaundryAvailabilityUseCase(repo)(MonitorMode.WASHERS, startedAt, now)

            assertEquals(0, repo.availabilityChecks)
        }

    @Test
    fun `a stale poll for a stopped monitor expires without hitting the network`() =
        runTest {
            val repo = useCase(Result.Success(true))
            val outcome = PollLaundryAvailabilityUseCase(repo)(MonitorMode.OFF, startedAt, startedAt + 1)

            assertEquals(PollOutcome.Expired, outcome)
            assertEquals(0, repo.availabilityChecks)
        }
}
