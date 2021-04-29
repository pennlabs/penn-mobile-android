package com.pennapps.labs.pennmobile.classes

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import org.joda.time.Interval

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