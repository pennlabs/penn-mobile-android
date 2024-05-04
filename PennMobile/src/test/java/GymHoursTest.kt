package com.pennapps.labs.pennmobile.classes

import org.joda.time.Interval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Unit tests
class GymHoursTest {
    @Before
    fun setup() {
        print("Setting up Unit tests")
    }

    @Test
    fun testSimple() {
        print("Testing...")
        assertTrue(true)
    }

    @Test
    fun testGymHours() {
        val gymHours = GymHours(true, null, null)
        val interval = gymHours.interval
        assertEquals(interval, Interval(0, 0))
    }
}
