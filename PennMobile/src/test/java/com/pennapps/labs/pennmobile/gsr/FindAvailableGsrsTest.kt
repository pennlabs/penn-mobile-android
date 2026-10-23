package com.pennapps.labs.pennmobile.gsr

import android.util.Log
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.domain.FindAvailableGsrs
import com.pennapps.labs.pennmobile.gsr.domain.GsrApiException
import com.pennapps.labs.pennmobile.gsr.domain.GsrNotLoggedInException
import com.pennapps.labs.pennmobile.gsr.domain.GsrOfflineException
import com.pennapps.labs.pennmobile.gsr.domain.GsrSearchResult
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

class FindAvailableGsrsTest {
    private class FakeGsrRepo(
        private val rooms: List<GsrRoom> = emptyList(),
        private val error: Exception? = null,
    ) : GsrRepo {
        val requests = mutableListOf<Triple<GsrLocation, LocalDate, LocalDate>>()

        override suspend fun getAvailability(
            location: GsrLocation,
            startDate: LocalDate,
            endDate: LocalDate,
        ): List<GsrRoom> {
            requests += Triple(location, startDate, endDate)
            error?.let { throw it }
            return rooms
        }

        override fun getSavedUserInfo() = Triple("", "", "")

        override suspend fun getLocations(): List<GsrLocation> = emptyList()

        override suspend fun bookGsr(
            startTime: String?,
            endTime: String?,
            gid: Int,
            roomId: Int,
            roomName: String,
            firstName: String,
            lastName: String,
            email: String,
        ) = Unit

        override suspend fun cancelGsr(
            bookingId: String?,
            isHuntsmanReservation: Boolean,
        ) = Unit

        override suspend fun getReservations(): List<GSRReservation> = emptyList()
    }

    @Before
    fun mockLog() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun unmockLog() = unmockkStatic(Log::class)

    @Test
    fun `free rooms are found earliest first`() =
        runTest {
            val repo = FakeGsrRepo(listOf(room("B", slot("14:00"), slot("14:30")), room("A", slot("15:00"), slot("15:30"))))

            val result = FindAvailableGsrs(repo)(VP_GROUND_FLOOR, at("13:00"), 60)

            assertTrue(result is GsrSearchResult.Found)
            assertEquals(listOf("B", "A"), (result as GsrSearchResult.Found).options.map { it.roomName })
        }

    @Test
    fun `nothing free for the whole window is NoneAvailable`() =
        runTest {
            val result = FindAvailableGsrs(FakeGsrRepo(listOf(room("A", slot("14:00")))))(VP_GROUND_FLOOR, at("13:00"), 60)

            assertEquals(GsrSearchResult.NoneAvailable, result)
        }

    @Test
    fun `3 hours at Amy Gutmann Hall is too long and never hits the network`() =
        runTest {
            val repo = FakeGsrRepo()

            val result = FindAvailableGsrs(repo)(AMY_GUTMANN, at("14:00"), 180)

            assertEquals(GsrSearchResult.DurationTooLong(180, 120, AMY_GUTMANN), result)
            assertTrue(repo.requests.isEmpty())
        }

    @Test
    fun `repository failures map to their own results`() =
        runTest {
            suspend fun resultFor(error: Exception) = FindAvailableGsrs(FakeGsrRepo(error = error))(VP_GROUND_FLOOR, at("13:00"), 30)

            assertEquals(GsrSearchResult.NotLoggedIn, resultFor(GsrNotLoggedInException()))
            assertEquals(GsrSearchResult.Offline, resultFor(GsrOfflineException()))
            assertEquals(GsrSearchResult.Failed("Error: Not enough credits"), resultFor(GsrApiException("Error: Not enough credits")))
            assertEquals(GsrSearchResult.Failed("Couldn't load rooms. Please try again."), resultFor(IllegalStateException("boom")))
        }

    @Test(expected = CancellationException::class)
    fun `cancellation is rethrown, not reported as a failure`() =
        runTest {
            FindAvailableGsrs(FakeGsrRepo(error = CancellationException("left screen")))(VP_GROUND_FLOOR, at("13:00"), 30)
            Unit
        }

    @Test
    fun `a normal search asks for one day`() =
        runTest {
            val repo = FakeGsrRepo()
            FindAvailableGsrs(repo)(VP_GROUND_FLOOR, at("14:00"), 120)

            assertEquals(Triple(VP_GROUND_FLOOR, DAY, DAY), repo.requests.single())
        }

    @Test
    fun `a window crossing midnight asks for both days`() =
        runTest {
            val repo = FakeGsrRepo()
            FindAvailableGsrs(repo)(VP_GROUND_FLOOR, at("23:00"), 120)

            assertEquals(Triple(VP_GROUND_FLOOR, DAY, DAY.plusDays(1)), repo.requests.single())
        }

    @Test
    fun `a window ending exactly at midnight stays on one day`() =
        runTest {
            val repo = FakeGsrRepo()
            FindAvailableGsrs(repo)(VP_GROUND_FLOOR, at("23:00"), 60)

            assertEquals(Triple(VP_GROUND_FLOOR, DAY, DAY), repo.requests.single())
        }

    @Test
    fun `dates are campus days even when the phone is in another time zone`() =
        runTest {
            val repo = FakeGsrRepo()
            // 02:00 UTC on Oct 10 is 22:00 on Oct 9 in Philadelphia.
            val fromUtc = ZonedDateTime.of(2026, 10, 10, 2, 0, 0, 0, ZoneOffset.UTC)
            FindAvailableGsrs(repo)(VP_GROUND_FLOOR, fromUtc, 30)

            assertEquals(DAY, repo.requests.single().second)
        }

    @Test(expected = IllegalArgumentException::class)
    fun `durations that aren't whole slots are rejected`() =
        runTest {
            FindAvailableGsrs(FakeGsrRepo())(VP_GROUND_FLOOR, at("13:00"), 45)
            Unit
        }
}
