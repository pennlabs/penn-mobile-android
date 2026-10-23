package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.domain.findBookableOptions
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration

class FindBookableOptionsTest {
    private val sixSlots =
        room("A", slot("14:00"), slot("14:30"), slot("15:00"), slot("15:30"), slot("16:00"), slot("16:30"))

    private fun startsOf(
        durationMinutes: Int,
        vararg rooms: GsrRoom,
        from: String = "13:00",
    ) = findBookableOptions(rooms.toList(), VP_GROUND_FLOOR, offsetAt(from), durationMinutes)
        .map { "${it.roomName} ${it.start.toLocalTime()}-${it.end.toLocalTime()}" }

    @Test
    fun `every window length from 30 to 180 minutes is built from back-to-back slots`() {
        for (minutes in 30..180 step 30) {
            val options = findBookableOptions(listOf(sixSlots), VP_GROUND_FLOOR, offsetAt("13:00"), minutes)
            assertEquals("windows of $minutes min", 6 - minutes / 30 + 1, options.size)
            assertTrue(options.all { Duration.between(it.start, it.end).toMinutes() == minutes.toLong() })
        }
    }

    @Test
    fun `an hour needs two touching slots`() {
        assertEquals(
            listOf("A 14:00-15:00", "A 14:30-15:30", "A 15:00-16:00"),
            startsOf(60, room("A", slot("14:00"), slot("14:30"), slot("15:00"), slot("15:30"))),
        )
    }

    @Test
    fun `a gap breaks a window`() {
        assertEquals(listOf("A 14:00-15:00"), startsOf(60, room("A", slot("14:00"), slot("14:30"), slot("15:30"))))
    }

    @Test
    fun `an unavailable slot breaks a window`() {
        val withTaken = room("A", slot("14:00"), slot("14:30", available = false), slot("15:00"))
        assertTrue(startsOf(60, withTaken).isEmpty())
        assertEquals(listOf("A 14:00-14:30", "A 15:00-15:30"), startsOf(30, withTaken))
    }

    @Test
    fun `slots ending at or before the start time are skipped`() {
        assertEquals(listOf("A 14:30-15:00"), startsOf(30, room("A", slot("14:00"), slot("14:30")), from = "14:30"))
    }

    @Test
    fun `a slot already in progress still counts`() {
        assertEquals(listOf("A 14:00-14:30", "A 14:30-15:00"), startsOf(30, room("A", slot("14:00"), slot("14:30")), from = "14:10"))
    }

    @Test
    fun `a window longer than the room's free run gives nothing`() {
        assertTrue(startsOf(120, room("A", slot("14:00"), slot("14:30"), slot("15:00"))).isEmpty())
    }

    @Test
    fun `a window can cross midnight`() {
        val nextDay = DAY.plusDays(1)
        val lateNight = room("A", slot("23:00"), slot("23:30"), slot("00:00", nextDay), slot("00:30", nextDay))
        assertEquals(listOf("A 23:00-01:00"), startsOf(120, lateNight, from = "22:00"))
    }

    @Test
    fun `results are earliest first, then by room name`() {
        assertEquals(
            listOf("A 13:30-14:00", "A 14:00-14:30", "B 14:00-14:30"),
            startsOf(30, room("B", slot("14:00")), room("A", slot("14:00"), slot("13:30"))),
        )
    }

    @Test
    fun `options use the location ids and keep the backend's end time`() {
        val last = slot("14:30")
        val option = findBookableOptions(listOf(room("A", slot("14:00"), last, id = 42)), VP_GROUND_FLOOR, offsetAt("13:00"), 60).single()

        assertEquals("1086", option.lid)
        assertEquals(4660, option.gid)
        assertEquals(42, option.roomId)
        assertTrue(option.end === last.end)
    }

    @Test
    fun `no rooms gives no options`() {
        assertTrue(startsOf(30).isEmpty())
    }
}
