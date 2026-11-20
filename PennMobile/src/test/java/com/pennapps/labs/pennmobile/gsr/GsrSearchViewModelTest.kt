package com.pennapps.labs.pennmobile.gsr

import android.util.Log
import com.pennapps.labs.pennmobile.gsr.domain.FindAvailableGsrs
import com.pennapps.labs.pennmobile.gsr.domain.GsrApiException
import com.pennapps.labs.pennmobile.gsr.domain.GsrNotLoggedInException
import com.pennapps.labs.pennmobile.gsr.domain.GsrOfflineException
import com.pennapps.labs.pennmobile.gsr.domain.GsrSearchResult
import com.pennapps.labs.pennmobile.gsr.domain.PENN_ZONE
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrBooked
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrContent
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrSearchViewModel
import com.pennapps.labs.pennmobile.gsr.repo.endTimeParam
import com.pennapps.labs.pennmobile.gsr.repo.startTimeParam
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GsrSearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val locations = listOf(VP_GROUND_FLOOR, HUNTSMAN, BIOTECH, AMY_GUTMANN)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    /** [now] (default 1:00 PM) on [DAY] in Philadelphia. */
    private fun TestScope.viewModel(
        repo: FakeGsrRepo,
        now: String = "13:00",
        clock: Clock = Clock.fixed(at(now).toInstant(), PENN_ZONE),
    ): GsrSearchViewModel = GsrSearchViewModel(repo, FindAvailableGsrs(repo), clock).also { advanceUntilIdle() }

    private fun GsrSearchViewModel.result() = (uiState.value.content as GsrContent.Loaded).result

    @Test
    fun `starts on today at the current half-hour and sorts by time`() =
        runTest(dispatcher) {
            val state = viewModel(FakeGsrRepo(locations = locations)).uiState.value

            assertEquals(DAY, state.date)
            assertEquals(LocalTime.of(13, 0), state.startTime)
            assertTrue(state.sortByTime)
        }

    @Test
    fun `the default start time rounds down and today offers only times from now on`() =
        runTest(dispatcher) {
            val state = viewModel(FakeGsrRepo(locations = locations), now = "13:20").uiState.value

            assertEquals(LocalTime.of(13, 0), state.startTime)
            assertEquals(LocalTime.of(13, 0), state.startTimeChoices.first())
            assertEquals(LocalTime.of(23, 30), state.startTimeChoices.last())
        }

    @Test
    fun `a later day offers the whole day and keeps an early time until switching back to today`() =
        runTest(dispatcher) {
            val vm = viewModel(FakeGsrRepo(locations = locations), now = "13:20")

            vm.selectDate(DAY.plusDays(1))
            vm.selectTime(LocalTime.of(9, 0))
            assertEquals(
                LocalTime.MIDNIGHT,
                vm.uiState.value.startTimeChoices
                    .first(),
            )
            assertEquals(48, vm.uiState.value.startTimeChoices.size)
            assertEquals(LocalTime.of(9, 0), vm.uiState.value.startTime)

            vm.selectDate(DAY)
            assertEquals(LocalTime.of(13, 0), vm.uiState.value.startTime)
            assertEquals(
                LocalTime.of(13, 0),
                vm.uiState.value.startTimeChoices
                    .first(),
            )
        }

    @Test
    fun `a past time today moves up to the current half-hour`() =
        runTest(dispatcher) {
            val vm = viewModel(FakeGsrRepo(locations = locations), now = "13:20")

            vm.selectTime(LocalTime.of(9, 0))

            assertEquals(LocalTime.of(13, 0), vm.uiState.value.startTime)
        }

    /** A clock the test can move forward, e.g. to reopen the tab later in the day. */
    private class MovableClock(
        var now: ZonedDateTime,
    ) : Clock() {
        override fun getZone(): ZoneId = PENN_ZONE

        override fun withZone(zone: ZoneId): Clock = this

        override fun instant(): Instant = now.toInstant()
    }

    @Test
    fun `reopening the tab goes back to now but keeps the location`() =
        runTest(dispatcher) {
            val clock = MovableClock(at("13:00"))
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo, clock = clock)
            vm.selectLocation(BIOTECH)
            vm.selectDate(DAY.plusDays(1))
            vm.selectTime(LocalTime.of(9, 0))
            advanceUntilIdle()

            clock.now = at("15:10")
            vm.resetToNow()
            advanceUntilIdle()

            val state = vm.uiState.value
            assertEquals(DAY, state.date)
            assertEquals(LocalTime.of(15, 0), state.startTime)
            assertEquals(LocalTime.of(15, 0), state.startTimeChoices.first())
            assertEquals(BIOTECH, state.selectedLocation)
            val (searched, from, _) = repo.availabilityRequests.last()
            assertEquals(BIOTECH to DAY, searched to from)
        }

    @Test
    fun `reopening the tab with nothing changed does not reload`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo)
            val requests = repo.availabilityRequests.size

            vm.resetToNow()
            advanceUntilIdle()

            assertEquals(requests, repo.availabilityRequests.size)
        }

    @Test
    fun `the last chosen location is restored`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, savedLocationKey = BIOTECH.key)

            val vm = viewModel(repo)

            assertEquals(BIOTECH, vm.uiState.value.selectedLocation)
            assertEquals(BIOTECH, repo.availabilityRequests.single().first)
        }

    @Test
    fun `a saved location the user can no longer book falls back to the first`() =
        runTest(dispatcher) {
            val vm = viewModel(FakeGsrRepo(locations = locations, savedLocationKey = "GONE|0"))

            assertEquals(VP_GROUND_FLOOR, vm.uiState.value.selectedLocation)
        }

    @Test
    fun `choosing a location saves it and searches there`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo)

            vm.selectLocation(HUNTSMAN)
            advanceUntilIdle()

            assertEquals(HUNTSMAN.key, repo.savedLocationKey)
            assertEquals(HUNTSMAN, repo.availabilityRequests.last().first)
        }

    @Test
    fun `switching to a location with a lower limit shortens the duration`() =
        runTest(dispatcher) {
            val vm = viewModel(FakeGsrRepo(locations = locations, savedLocationKey = BIOTECH.key))
            vm.selectDuration(180)
            advanceUntilIdle()

            vm.selectLocation(HUNTSMAN)
            advanceUntilIdle()

            assertEquals(90, vm.uiState.value.durationMinutes)
            assertEquals(listOf(30, 60, 90), vm.uiState.value.durationOptions)
        }

    @Test
    fun `failing to load locations shows why`() =
        runTest(dispatcher) {
            fun resultFor(error: Exception) = viewModel(FakeGsrRepo(locationsError = error)).result()

            assertEquals(GsrSearchResult.NotLoggedIn, resultFor(GsrNotLoggedInException()))
            assertEquals(GsrSearchResult.Offline, resultFor(GsrOfflineException()))
            assertEquals(GsrSearchResult.Failed("Wharton: down"), resultFor(GsrApiException("Wharton: down")))
        }

    @Test
    fun `an account with no locations gets an explanation`() =
        runTest(dispatcher) {
            val result = viewModel(FakeGsrRepo(locations = emptyList())).result()

            assertTrue(result is GsrSearchResult.Failed)
        }

    @Test
    fun `search results and failures reach the screen`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, rooms = listOf(room("L15", slot("14:00"))))
            val vm = viewModel(repo)
            assertTrue(vm.result() is GsrSearchResult.Found)
            assertEquals(listOf("L15"), (vm.uiState.value.content as GsrContent.Loaded).rooms.map { it.roomName })

            vm.selectDuration(60)
            advanceUntilIdle()
            assertEquals(GsrSearchResult.NoneAvailable, vm.result())

            repo.availabilityError = GsrOfflineException()
            vm.refresh()
            advanceUntilIdle()
            assertEquals(GsrSearchResult.Offline, vm.result())
        }

    @Test
    fun `changing the sort reorders without another request`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, rooms = listOf(room("103A", slot("16:00")), room("F66", slot("14:00"))))
            val vm = viewModel(repo)

            fun names() = (vm.uiState.value.content as GsrContent.Loaded).rooms.map { it.roomName }
            assertEquals(listOf("F66", "103A"), names())

            vm.setSortByTime(false)

            assertEquals(listOf("103A", "F66"), names())
            assertEquals(1, repo.availabilityRequests.size)
        }

    @Test
    fun `refresh searches again and clears the refreshing flag`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo)
            val pending = CompletableDeferred<Unit>()
            repo.availabilityGates.add(pending)

            vm.refresh()
            advanceUntilIdle()
            assertTrue(vm.uiState.value.isRefreshing)
            assertTrue("pull-to-refresh keeps old results visible", vm.uiState.value.content is GsrContent.Loaded)

            pending.complete(Unit)
            advanceUntilIdle()

            assertEquals(2, repo.availabilityRequests.size)
            assertFalse(vm.uiState.value.isRefreshing)
        }

    @Test
    fun `refresh after locations failed reloads them`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, locationsError = GsrOfflineException())
            val vm = viewModel(repo)
            assertEquals(GsrSearchResult.Offline, vm.result())

            repo.locationsError = null
            vm.refresh()
            advanceUntilIdle()

            assertEquals(2, repo.locationRequests)
            assertEquals(VP_GROUND_FLOOR, vm.uiState.value.selectedLocation)
        }

    @Test
    fun `a newer search wins even if the older one finishes last`() =
        runTest(dispatcher) {
            val older = CompletableDeferred<Unit>()
            val newer = CompletableDeferred<Unit>()
            // 30 minutes finds L15; 60 minutes finds nothing.
            val repo = FakeGsrRepo(locations = locations, rooms = listOf(room("L15", slot("14:00"))))
            repo.availabilityGates.addAll(listOf(older, newer))

            val vm = viewModel(repo)
            vm.selectDuration(60)
            advanceUntilIdle()

            newer.complete(Unit)
            advanceUntilIdle()
            older.complete(Unit)
            advanceUntilIdle()

            assertEquals(GsrSearchResult.NoneAvailable, vm.result())
        }

    @Test
    fun `dates stay within the location's bookable days`() =
        runTest(dispatcher) {
            val vm = viewModel(FakeGsrRepo(locations = locations))
            assertEquals(DAY.plusDays(6), vm.uiState.value.lastBookableDate)

            vm.selectDate(DAY.plusDays(30))
            assertEquals(DAY.plusDays(6), vm.uiState.value.date)

            vm.selectDate(DAY.minusDays(3))
            assertEquals(DAY, vm.uiState.value.date)
        }

    private val l15 = GsrBookingOption("1086", 4660, 15, "L15", offsetAt("14:00"), offsetAt("15:30"))

    @Test
    fun `tapping a slot opens the sheet with the saved name`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, userInfo = Triple("Ben", "Franklin", "ben@upenn.edu"))
            val vm = viewModel(repo)

            vm.openBooking(l15)

            assertEquals(l15, vm.uiState.value.pendingBooking)
            assertEquals("Ben Franklin", vm.uiState.value.bookerName)
            assertTrue("opening the sheet doesn't book anything", repo.bookings.isEmpty())
        }

    @Test
    fun `with no saved name the sheet shows the PennKey, and with neither it hides the row`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, pennKey = "bfranklin")
            val vm = viewModel(repo)

            vm.openBooking(l15)
            assertEquals("bfranklin", vm.uiState.value.bookerName)

            repo.pennKey = null
            vm.openBooking(l15)
            assertNull(vm.uiState.value.bookerName)
        }

    @Test
    fun `cancel closes the sheet without booking`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo)
            vm.openBooking(l15)

            vm.dismissBooking()

            assertNull(vm.uiState.value.pendingBooking)
            assertTrue(repo.bookings.isEmpty())
        }

    @Test
    fun `a successful booking closes the sheet, reports it once and refreshes the list`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations)
            val vm = viewModel(repo)
            val gate = CompletableDeferred<Unit>()
            repo.bookingGates.add(gate)
            val events = mutableListOf<GsrBooked>()
            val collector = backgroundScope.launch { vm.bookingEvents.toList(events) }
            vm.openBooking(l15)

            vm.confirmBooking()
            advanceUntilIdle()
            assertTrue(vm.uiState.value.isBooking)
            vm.dismissBooking()
            assertEquals("the sheet can't be dismissed mid-request", l15, vm.uiState.value.pendingBooking)
            vm.confirmBooking()

            gate.complete(Unit)
            advanceUntilIdle()

            val state = vm.uiState.value
            assertNull(state.pendingBooking)
            assertFalse(state.isBooking)
            assertEquals(listOf(GsrBooked("L15")), events)
            assertEquals("a repeat tap mid-request doesn't book twice", 1, repo.bookings.size)
            assertEquals(Triple(l15.startTimeParam(), l15.endTimeParam(), 15), repo.bookings.single())
            assertEquals(2, repo.availabilityRequests.size)
            collector.cancel()
        }

    @Test
    fun `a failed booking keeps the sheet open with the reason`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, bookingError = Exception("Error: Not enough credits"))
            val vm = viewModel(repo)
            vm.openBooking(l15)

            vm.confirmBooking()
            advanceUntilIdle()

            val state = vm.uiState.value
            assertEquals(l15, state.pendingBooking)
            assertFalse(state.isBooking)
            assertEquals("Error: Not enough credits", state.bookingError)
            assertEquals("no refresh after a failure", 1, repo.availabilityRequests.size)

            repo.bookingError = null
            vm.confirmBooking()
            advanceUntilIdle()
            assertNull(vm.uiState.value.bookingError)
            assertNull(vm.uiState.value.pendingBooking)
            assertEquals(GsrBooked("L15"), vm.bookingEvents.first())
        }

    @Test
    fun `an expired login during booking asks the user to log in`() =
        runTest(dispatcher) {
            val repo = FakeGsrRepo(locations = locations, bookingError = GsrNotLoggedInException())
            val vm = viewModel(repo)
            vm.openBooking(l15)

            vm.confirmBooking()
            advanceUntilIdle()
            assertTrue(vm.uiState.value.bookingNeedsLogin)

            vm.dismissBooking()
            assertFalse(vm.uiState.value.bookingNeedsLogin)
        }
}
