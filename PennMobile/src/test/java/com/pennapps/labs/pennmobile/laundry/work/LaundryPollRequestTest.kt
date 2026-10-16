package com.pennapps.labs.pennmobile.laundry.work

import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * WorkRequest construction is covered by this JVM test, and polling decisions are covered through
 * use-case tests. Direct assisted-worker execution is not covered because the project currently
 * lacks a Robolectric/WorkManager runtime and test WorkerFactory.
 */
class LaundryPollRequestTest {
    @Test
    fun `polls on the configured interval`() {
        val request = LaundryPollRequest.build(MonitorMode.WASHERS, startedAtMillis = 42L)

        assertEquals(
            TimeUnit.MINUTES.toMillis(LaundryPollRequest.POLL_INTERVAL_MINUTES),
            request.workSpec.initialDelay,
        )
    }

    @Test
    fun `carries the mode and start time through to the worker`() {
        val request = LaundryPollRequest.build(MonitorMode.DRYERS, startedAtMillis = 42L)
        val input = request.workSpec.input

        assertEquals(MonitorMode.DRYERS.prefValue, input.getString(LaundryPollRequest.KEY_MODE))
        assertEquals(42L, input.getLong(LaundryPollRequest.KEY_STARTED_AT, 0L))
    }

    @Test
    fun `the monitoring window is one hour`() {
        assertEquals(TimeUnit.HOURS.toMillis(1), LaundryPollRequest.MONITOR_WINDOW_MS)
    }
}
