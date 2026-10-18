package com.pennapps.labs.pennmobile.laundry.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MonitorModeTest {
    @Test
    fun `pref values round trip`() {
        MonitorMode.entries.forEach { mode ->
            assertEquals(mode, MonitorMode.fromPrefValue(mode.prefValue))
        }
    }

    @Test
    fun `unrecognised pref values mean not monitoring`() {
        listOf(null, "", "washers", "GARBAGE", "off").forEach { value ->
            assertEquals("expected OFF for $value", MonitorMode.OFF, MonitorMode.fromPrefValue(value))
        }
    }

    @Test
    fun `machine labels are singular`() {
        assertEquals("washer", MonitorMode.WASHERS.machineLabel)
        assertEquals("dryer", MonitorMode.DRYERS.machineLabel)
    }
}
