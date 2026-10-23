package com.pennapps.labs.pennmobile.gsr

import android.util.Log
import com.google.gson.Gson
import com.pennapps.labs.pennmobile.gsr.classes.GSR
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.repo.endTimeParam
import com.pennapps.labs.pennmobile.gsr.repo.formatGsrTime
import com.pennapps.labs.pennmobile.gsr.repo.parseGsrTime
import com.pennapps.labs.pennmobile.gsr.repo.startTimeParam
import com.pennapps.labs.pennmobile.gsr.repo.toDomain
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GsrMappersTest {
    private val gson = Gson()

    @Before
    fun mockLog() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
    }

    @After
    fun unmockLog() = unmockkStatic(Log::class)

    @Test
    fun `daylight and standard time offsets parse and format back unchanged`() {
        for (raw in listOf("2026-10-09T14:00:00-04:00", "2026-12-09T14:30:00-05:00", "2026-10-09T18:00:00+00:00")) {
            assertEquals(raw, formatGsrTime(parseGsrTime(raw)!!))
        }
    }

    @Test
    fun `booking params keep seconds that ISO formatting would drop`() {
        val option =
            GsrBookingOption(
                lid = "1086",
                gid = 4660,
                roomId = 1,
                roomName = "A",
                start = parseGsrTime("2026-10-09T14:00:00-04:00")!!,
                end = parseGsrTime("2026-10-09T15:00:00-04:00")!!,
            )

        assertEquals("2026-10-09T14:00:00-04:00", option.startTimeParam())
        assertEquals("2026-10-09T15:00:00-04:00", option.endTimeParam())
    }

    @Test
    fun `unparseable times give null`() {
        assertNull(parseGsrTime("2026-10-09 14:00"))
        assertNull(parseGsrTime(null))
    }

    @Test
    fun `libcal slots without an available flag are available`() {
        val json =
            """
            {"rooms": [{"room_name": "Room 101", "id": 5, "availability": [
              {"start_time": "2026-10-09T14:00:00-04:00", "end_time": "2026-10-09T14:30:00-04:00"},
              {"start_time": "2026-10-09T14:30:00-04:00", "end_time": "2026-10-09T15:00:00-04:00", "available": false}
            ]}]}
            """.trimIndent()

        val room =
            gson
                .fromJson(json, GSR::class.java)
                .rooms!!
                .single()
                .toDomain()!!

        assertEquals(listOf(true, false), room.slots.map { it.isAvailable })
        assertEquals(0, room.gid)
    }

    @Test
    fun `rooms missing an id or name and slots with bad times are skipped`() {
        val json =
            """
            {"rooms": [
              {"room_name": "No id", "availability": []},
              {"id": 6, "availability": []},
              {"room_name": "Ok", "id": 7, "availability": [
                {"start_time": "garbage", "end_time": "2026-10-09T14:30:00-04:00"},
                {"start_time": "2026-10-09T14:30:00-04:00", "end_time": "2026-10-09T15:00:00-04:00"}
              ]}
            ]}
            """.trimIndent()

        val rooms = gson.fromJson(json, GSR::class.java).rooms!!.mapNotNull { it.toDomain() }

        assertEquals(listOf("Ok"), rooms.map { it.name })
        assertEquals(1, rooms.single().slots.size)
    }

    @Test
    fun `location kinds map, including seas and unknown values`() {
        fun kindOf(kind: String?) = location(kind = kind)?.kind

        assertEquals(GsrLocation.Kind.WHARTON, kindOf("WHARTON"))
        assertEquals(GsrLocation.Kind.LIBCAL, kindOf("LIBCAL"))
        assertEquals(GsrLocation.Kind.PENNGRP, kindOf("PENNGRP"))
        assertEquals(GsrLocation.Kind.UNKNOWN, kindOf("SOMETHING_NEW"))
        assertEquals(GsrLocation.Kind.UNKNOWN, kindOf(null))
    }

    @Test
    fun `locations without a lid or name are dropped`() {
        assertNull(location(lid = null))
        assertNull(location(name = " "))
        assertTrue(location() != null)
    }

    @Test
    fun `a null gid passes through as 0`() {
        val parsed = gson.fromJson("""{"lid": "AGH", "gid": null, "name": "AGH", "kind": "PENNGRP"}""", GSRLocation::class.java)

        assertEquals(0, parsed.toDomain()!!.gid)
    }

    private fun location(
        lid: String? = "1086",
        name: String? = "Weigle",
        kind: String? = "LIBCAL",
    ): GsrLocation? =
        GSRLocation()
            .apply {
                this.id = lid
                this.name = name
                this.kind = kind
                this.gid = 1889
            }.toDomain()
}
