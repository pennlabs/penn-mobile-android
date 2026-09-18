package com.pennapps.labs.pennmobile.laundry

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.compose.utils.SnackBarEvent
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.usecase.StartLaundryMonitorUseCase
import com.pennapps.labs.pennmobile.laundry.domain.usecase.StopLaundryMonitorUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * This test is the point of the refactor: none of it was writable while the monitoring logic lived
 * in LaundryFragment behind viewLifecycleOwner.lifecycleScope.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LaundryMonitorViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(availability: Result<Boolean>): Pair<FakeLaundryMonitorRepo, LaundryMonitorViewModel> {
        val repo = FakeLaundryMonitorRepo(availability)
        val scheduler = FakeLaundryMonitorScheduler()
        val start = StartLaundryMonitorUseCase(repo, scheduler, StopLaundryMonitorUseCase(repo, scheduler))
        return repo to LaundryMonitorViewModel(repo, start)
    }

    @Test
    fun `monitoring a busy machine keeps the mode and says so`() =
        runTest {
            val (repo, viewModel) = viewModel(Result.Success(false))

            viewModel.onModeSelected(MonitorMode.WASHERS)
            advanceUntilIdle()

            assertEquals(MonitorMode.WASHERS, repo.monitorMode.value)
            assertEquals(SnackBarEvent.Success("Monitoring washers for 1 hour"), viewModel.snackBarEvent.value)
        }

    @Test
    fun `an already free machine reverts the mode and reads grammatically`() =
        runTest {
            val (repo, viewModel) = viewModel(Result.Success(true))

            viewModel.onModeSelected(MonitorMode.WASHERS)
            advanceUntilIdle()

            assertEquals(MonitorMode.OFF, repo.monitorMode.value)
            assertEquals(SnackBarEvent.Success("A washer is already available!"), viewModel.snackBarEvent.value)
        }

    @Test
    fun `a failed check surfaces as an error rather than silent success`() =
        runTest {
            val (repo, viewModel) = viewModel(Result.Error("You are offline."))

            viewModel.onModeSelected(MonitorMode.DRYERS)
            advanceUntilIdle()

            assertEquals(MonitorMode.OFF, repo.monitorMode.value)
            assertEquals(SnackBarEvent.Error("You are offline."), viewModel.snackBarEvent.value)
        }

    @Test
    fun `messages are shown once`() =
        runTest {
            val (_, viewModel) = viewModel(Result.Success(false))

            viewModel.onModeSelected(MonitorMode.WASHERS)
            advanceUntilIdle()
            viewModel.resetSnackBarEvent()

            assertTrue(viewModel.snackBarEvent.value is SnackBarEvent.None)
        }
}
