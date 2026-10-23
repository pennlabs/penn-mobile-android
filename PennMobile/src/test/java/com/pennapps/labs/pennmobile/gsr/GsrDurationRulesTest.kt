package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.domain.durationOptions
import com.pennapps.labs.pennmobile.gsr.domain.maxDurationMinutes
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import org.junit.Assert.assertEquals
import org.junit.Test

class GsrDurationRulesTest {
    @Test
    fun `wharton rooms allow 90 minutes`() {
        assertEquals(90, HUNTSMAN.maxDurationMinutes())
        assertEquals(listOf(30, 60, 90), HUNTSMAN.durationOptions())
    }

    @Test
    fun `biotech commons allows 3 hours`() {
        assertEquals(180, BIOTECH.maxDurationMinutes())
        assertEquals(listOf(30, 60, 90, 120, 150, 180), BIOTECH.durationOptions())
    }

    @Test
    fun `biotech is matched by lid and gid together`() {
        assertEquals(120, BIOTECH.copy(gid = 1).maxDurationMinutes())
    }

    @Test
    fun `libcal, seas and unknown kinds allow 2 hours`() {
        assertEquals(120, VP_GROUND_FLOOR.maxDurationMinutes())
        assertEquals(120, AMY_GUTMANN.maxDurationMinutes())
        assertEquals(120, VP_GROUND_FLOOR.copy(kind = GsrLocation.Kind.UNKNOWN).maxDurationMinutes())
        assertEquals(listOf(30, 60, 90, 120), AMY_GUTMANN.durationOptions())
    }
}
