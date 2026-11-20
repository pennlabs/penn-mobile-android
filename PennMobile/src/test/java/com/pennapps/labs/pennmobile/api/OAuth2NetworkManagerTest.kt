package com.pennapps.labs.pennmobile.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.classes.AccessToken
import com.pennapps.labs.pennmobile.api.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.api.classes.AuthEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class OAuth2NetworkManagerTest {
    private val studentLife = mockk<StudentLife>()
    private val prefs = FakeSharedPreferences()
    private val context =
        mockk<Context> {
            every { getString(any()) } answers { "pref_${firstArg<Int>()}" }
        }

    private fun key(id: Int) = "pref_$id"

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>(), any()) } returns 0
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    private fun TestScope.manager() =
        OAuth2NetworkManager(context, CoroutineScope(UnconfinedTestDispatcher(testScheduler)), prefs, studentLife)

    private fun signedIn(expiresAt: Long) {
        prefs.edit().apply {
            putString(key(R.string.access_token), "old-token")
            putString(key(R.string.refresh_token), "refresh")
            putString(key(R.string.expires_in), "3600")
            putLong(key(R.string.token_expires_at), expiresAt)
        }
    }

    private suspend fun TestScope.eventsDuring(
        manager: OAuth2NetworkManager,
        block: suspend () -> Unit,
    ): List<AuthEvent> {
        val events = mutableListOf<AuthEvent>()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) { manager.authEvents.collect { events += it } }
        block()
        collector.cancel()
        return events
    }

    @Test
    fun `a valid token is returned without refreshing`() =
        runTest {
            signedIn(expiresAt = Long.MAX_VALUE)

            assertEquals(AccessToken.Valid("old-token"), manager().getAccessTokenResult())
            coVerify(exactly = 0) { studentLife.refreshAccessToken(any(), any(), any()) }
        }

    @Test
    fun `guest mode is reported as Guest`() =
        runTest {
            prefs.edit().putBoolean(key(R.string.guest_mode), true)

            assertEquals(AccessToken.Guest, manager().getAccessTokenResult())
        }

    @Test
    fun `no refresh token means logged out and asks for login`() =
        runTest {
            val manager = manager()
            var result: AccessToken? = null

            val events = eventsDuring(manager) { result = manager.getAccessTokenResult() }

            assertEquals(AccessToken.LoggedOut, result)
            assertEquals(listOf(AuthEvent.RequiresLogin), events)
        }

    @Test
    fun `a rejected refresh means logged out and asks for login`() =
        runTest {
            signedIn(expiresAt = 0)
            coEvery { studentLife.refreshAccessToken(any(), any(), any()) } returns
                Response.error(400, """{"error":"invalid_grant"}""".toResponseBody("application/json".toMediaType()))
            val manager = manager()
            var result: AccessToken? = null

            val events = eventsDuring(manager) { result = manager.getAccessTokenResult() }

            assertEquals(AccessToken.LoggedOut, result)
            assertEquals(listOf(AuthEvent.RequiresLogin), events)
        }

    @Test
    fun `a refresh that fails offline is not treated as logged out`() =
        runTest {
            signedIn(expiresAt = 0)
            coEvery { studentLife.refreshAccessToken(any(), any(), any()) } throws UnknownHostException("pennmobile.org")
            val manager = manager()
            var result: AccessToken? = null

            val events = eventsDuring(manager) { result = manager.getAccessTokenResult() }

            assertEquals(AccessToken.NetworkUnavailable, result)
            assertTrue("offline must not send the user to login", events.isEmpty())
            assertNull(manager.getAccessToken())
        }

    @Test
    fun `a successful refresh returns and stores the new token`() =
        runTest {
            signedIn(expiresAt = 0)
            val refreshed =
                AccessTokenResponse().apply {
                    accessToken = "new-token"
                    refreshToken = "new-refresh"
                    expiresIn = "3600"
                }
            coEvery { studentLife.refreshAccessToken(any(), any(), any()) } returns Response.success(refreshed)

            assertEquals("new-token", manager().getAccessToken())
            assertEquals("new-refresh", prefs.getString(key(R.string.refresh_token), null))
        }

    @Test
    fun `the callback version releases its lock after an offline refresh`() =
        runTest {
            signedIn(expiresAt = 0)
            coEvery { studentLife.refreshAccessToken(any(), any(), any()) } throws UnknownHostException("pennmobile.org")
            val manager = manager()
            var firstRan = false
            manager.getAccessToken { firstRan = true }

            // Before the fix the mutex stayed locked here and this callback never ran.
            prefs.edit().putLong(key(R.string.token_expires_at), Long.MAX_VALUE)
            var secondRan = false
            manager.getAccessToken { secondRan = true }
            testScheduler.advanceUntilIdle()

            assertEquals(false, firstRan)
            assertTrue(secondRan)
        }

    @Test
    fun `overlapping callers share one refresh instead of reusing the old refresh token`() =
        runTest {
            signedIn(expiresAt = 0)
            val gate = CompletableDeferred<Unit>()
            val refreshed =
                AccessTokenResponse().apply {
                    accessToken = "new-token"
                    refreshToken = "new-refresh"
                    expiresIn = "3600"
                }
            // Like the server: the old refresh token works once, then is rejected.
            var used = false
            coEvery { studentLife.refreshAccessToken(any(), any(), any()) } coAnswers {
                gate.await()
                if (used) {
                    Response.error(400, """{"error":"invalid_grant"}""".toResponseBody("application/json".toMediaType()))
                } else {
                    used = true
                    Response.success(refreshed)
                }
            }
            val manager = manager()
            var suspendResult: AccessToken? = null
            var callbackRan = false

            val events =
                eventsDuring(manager) {
                    val first = launch(UnconfinedTestDispatcher(testScheduler)) { suspendResult = manager.getAccessTokenResult() }
                    manager.getAccessToken { callbackRan = true }
                    gate.complete(Unit)
                    first.join()
                    testScheduler.advanceUntilIdle()
                }

            assertEquals(AccessToken.Valid("new-token"), suspendResult)
            assertTrue(callbackRan)
            assertTrue("no caller should be sent to login", events.isEmpty())
            coVerify(exactly = 1) { studentLife.refreshAccessToken(any(), any(), any()) }
        }
}

/** In-memory SharedPreferences; edits apply immediately so tests don't need commit()/apply(). */
private class FakeSharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values

    override fun getString(
        key: String?,
        defValue: String?,
    ) = values[key] as? String ?: defValue

    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? {
        @Suppress("UNCHECKED_CAST")
        return (values[key] as? MutableSet<String>) ?: defValues
    }

    override fun getInt(
        key: String?,
        defValue: Int,
    ) = values[key] as? Int ?: defValue

    override fun getLong(
        key: String?,
        defValue: Long,
    ) = values[key] as? Long ?: defValue

    override fun getFloat(
        key: String?,
        defValue: Float,
    ) = values[key] as? Float ?: defValue

    override fun getBoolean(
        key: String?,
        defValue: Boolean,
    ) = values[key] as? Boolean ?: defValue

    override fun contains(key: String?) = values.containsKey(key)

    override fun edit(): SharedPreferences.Editor = Editor()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit

    private inner class Editor : SharedPreferences.Editor {
        private fun put(
            key: String?,
            value: Any?,
        ) = apply { values[key!!] = value }

        override fun putString(
            key: String?,
            value: String?,
        ) = put(key, value)

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?,
        ) = put(key, values)

        override fun putInt(
            key: String?,
            value: Int,
        ) = put(key, value)

        override fun putLong(
            key: String?,
            value: Long,
        ) = put(key, value)

        override fun putFloat(
            key: String?,
            value: Float,
        ) = put(key, value)

        override fun putBoolean(
            key: String?,
            value: Boolean,
        ) = put(key, value)

        override fun remove(key: String?) = apply { values.remove(key) }

        override fun clear() = apply { values.clear() }

        override fun commit() = true

        override fun apply() = Unit
    }
}
