package com.pennapps.labs.pennmobile.gsr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.classes.AccessToken
import com.pennapps.labs.pennmobile.gsr.classes.GSR
import com.pennapps.labs.pennmobile.gsr.classes.GSRBookingResult
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation
import com.pennapps.labs.pennmobile.gsr.domain.GsrApiException
import com.pennapps.labs.pennmobile.gsr.domain.GsrNotLoggedInException
import com.pennapps.labs.pennmobile.gsr.domain.GsrOfflineException
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepoImpl
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class GsrRepoImplTest {
    private val studentLife = mockk<StudentLife>()
    private val oAuth = mockk<OAuth2NetworkManager>()
    private val context = mockk<Context>(relaxed = true)
    private val repo =
        GsrRepoImpl(
            sharedPreferences = mockk<SharedPreferences>(relaxed = true),
            context = context,
            studentLife = studentLife,
            oAuth2NetworkManager = oAuth,
            appScope = CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher()),
        )

    private val roomsJson =
        """
        {"rooms": [{"room_name": "Room 101", "id": 5, "availability": [
          {"start_time": "2026-10-09T14:00:00-04:00", "end_time": "2026-10-09T14:30:00-04:00"}
        ]}]}
        """.trimIndent()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        coEvery { oAuth.getAccessTokenResult() } returns AccessToken.Valid("tok")
        mockkObject(GsrReservationWidget.Companion)
        every { GsrReservationWidget.requestUpdate(any()) } just Runs
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setPackage(any()) } answers { self as Intent }
    }

    @After
    fun tearDown() = unmockkAll()

    private fun <T> errorBody(
        code: Int,
        body: String,
    ): Response<T> = Response.error(code, body.toResponseBody("application/json".toMediaType()))

    private suspend inline fun <reified T : Throwable> assertThrows(block: () -> Unit): T {
        try {
            block()
        } catch (e: Throwable) {
            assertTrue("expected ${T::class.simpleName} but was $e", e is T)
            return e as T
        }
        throw AssertionError("expected ${T::class.simpleName}")
    }

    @Test
    fun `guests and signed-out users get NotLoggedIn without any request`() =
        runTest {
            for (state in listOf(AccessToken.Guest, AccessToken.LoggedOut)) {
                coEvery { oAuth.getAccessTokenResult() } returns state
                assertThrows<GsrNotLoggedInException> { repo.getAvailability(VP_GROUND_FLOOR, DAY) }
                assertThrows<GsrNotLoggedInException> { repo.getLocations() }
            }
            coVerify(exactly = 0) { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) }
            coVerify(exactly = 0) { studentLife.getGsrLocations(any()) }
        }

    @Test
    fun `a token refresh that failed offline is reported as offline`() =
        runTest {
            coEvery { oAuth.getAccessTokenResult() } returns AccessToken.NetworkUnavailable

            assertThrows<GsrOfflineException> { repo.getAvailability(VP_GROUND_FLOOR, DAY) }
        }

    @Test
    fun `a network error during the request is reported as offline`() =
        runTest {
            coEvery { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) } throws UnknownHostException("pennmobile.org")

            assertThrows<GsrOfflineException> { repo.getAvailability(VP_GROUND_FLOOR, DAY) }
        }

    @Test
    fun `availability sends the bearer token, ids and a single start date`() =
        runTest {
            coEvery { studentLife.getGsrAvailability("Bearer tok", "1086", 4660, "2026-10-09", null) } returns
                Response.success(Gson().fromJson(roomsJson, GSR::class.java))

            val rooms = repo.getAvailability(VP_GROUND_FLOOR, DAY)

            assertEquals(listOf("Room 101"), rooms.map { it.name })
            assertEquals(1, rooms.single().slots.size)
        }

    @Test
    fun `availability sends an end date only when it differs`() =
        runTest {
            coEvery { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) } returns Response.success(GSR())

            repo.getAvailability(VP_GROUND_FLOOR, DAY, DAY.plusDays(1))

            coVerify { studentLife.getGsrAvailability("Bearer tok", "1086", 4660, "2026-10-09", "2026-10-10") }
        }

    @Test
    fun `a missing body or rooms list means no rooms`() =
        runTest {
            coEvery { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) } returns Response.success(null)
            assertTrue(repo.getAvailability(VP_GROUND_FLOOR, DAY).isEmpty())

            coEvery { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) } returns Response.success(GSR())
            assertTrue(repo.getAvailability(VP_GROUND_FLOOR, DAY).isEmpty())
        }

    @Test
    fun `availability surfaces the backend's error reason`() =
        runTest {
            coEvery { studentLife.getGsrAvailability(any(), any(), any(), any(), any()) } returns
                errorBody(400, """{"error": "LibCal: Connection timeout"}""")

            val error = assertThrows<GsrApiException> { repo.getAvailability(VP_GROUND_FLOOR, DAY) }
            assertEquals("LibCal: Connection timeout", error.message)
        }

    @Test
    fun `locations are mapped and incomplete ones dropped`() =
        runTest {
            val ok =
                GSRLocation().apply {
                    id = "1086"
                    name = "Weigle"
                    gid = 1889
                    kind = "LIBCAL"
                }
            val noLid = GSRLocation().apply { name = "Broken" }
            coEvery { studentLife.getGsrLocations("Bearer tok") } returns Response.success(listOf(ok, noLid))

            assertEquals(listOf("Weigle"), repo.getLocations().map { it.name })
        }

    @Test
    fun `booking shows the backend's reason when it refuses`() =
        runTest {
            coEvery { studentLife.bookGSR(any(), any(), any(), any(), any(), any()) } returns
                errorBody(400, """{"error": "Error: Not enough credits"}""")

            val error = assertThrows<GsrApiException> { book() }
            assertEquals("Error: Not enough credits", error.message)
        }

    @Test
    fun `booking keeps the generic message when the error body is unreadable`() =
        runTest {
            coEvery { studentLife.bookGSR(any(), any(), any(), any(), any(), any()) } returns errorBody(500, "<html>oops</html>")

            val error = assertThrows<Exception> { book() }
            assertFalse(error is GsrApiException)
            assertEquals("Server returned an error. Please try again.", error.message)
        }

    @Test
    fun `booking while signed out keeps the original message`() =
        runTest {
            coEvery { oAuth.getAccessTokenResult() } returns AccessToken.LoggedOut

            val error = assertThrows<GsrNotLoggedInException> { book() }
            assertEquals("Authentication failed. Please log in again.", error.message)
        }

    @Test
    fun `a successful booking does not throw`() =
        runTest {
            val success = Gson().fromJson("""{"detail": "success"}""", GSRBookingResult::class.java)
            coEvery { studentLife.bookGSR("Bearer tok", "s", "e", 4660, 5, "Room 101") } returns Response.success(success)

            book()

            verify { GsrReservationWidget.requestUpdate(context) }
            verify { context.sendBroadcast(any<Intent>()) }
        }

    @Test
    fun `a failed booking leaves the widget and reservations alone`() =
        runTest {
            coEvery { studentLife.bookGSR(any(), any(), any(), any(), any(), any()) } returns
                errorBody(400, """{"error": "Error: Not enough credits"}""")

            assertThrows<GsrApiException> { book() }

            verify(exactly = 0) { GsrReservationWidget.requestUpdate(any()) }
            verify(exactly = 0) { context.sendBroadcast(any<Intent>()) }
        }

    @Test
    fun `leaving the screen mid-request does not cancel the booking`() =
        runTest {
            val serverReplied = CompletableDeferred<Unit>()
            val success = Gson().fromJson("""{"detail": "success"}""", GSRBookingResult::class.java)
            coEvery { studentLife.bookGSR(any(), any(), any(), any(), any(), any()) } coAnswers {
                serverReplied.await()
                Response.success(success)
            }

            val screen = launch { book() }
            testScheduler.runCurrent()
            screen.cancel()
            serverReplied.complete(Unit)

            verify { GsrReservationWidget.requestUpdate(context) }
            verify { context.sendBroadcast(any<Intent>()) }
        }

    private suspend fun book() =
        repo.bookGsr(
            startTime = "s",
            endTime = "e",
            gid = 4660,
            roomId = 5,
            roomName = "Room 101",
        )
}
