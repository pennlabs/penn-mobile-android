package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.domain.RoomNameComparator
import com.pennapps.labs.pennmobile.gsr.domain.groupByRoom
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GsrRoomGroupingTest {
    private fun option(
        room: String,
        start: String,
        roomId: Int = room.hashCode(),
    ) = GsrBookingOption("JMHH", 1, roomId, room, offsetAt(start), offsetAt(start).plusMinutes(30))

    @Test
    fun `sorting by time puts the room that frees up soonest first`() {
        val options = listOf(option("241", "21:00"), option("F66", "19:30"), option("241", "20:00"), option("103A", "20:00"))

        val rooms = options.groupByRoom(sortByTime = true)

        assertEquals(listOf("F66", "103A", "241"), rooms.map { it.roomName })
    }

    @Test
    fun `rooms opening at the same time fall back to name order`() {
        val rooms = listOf(option("F66", "20:00"), option("206", "20:00"), option("103A", "20:00")).groupByRoom(true)

        assertEquals(listOf("103A", "206", "F66"), rooms.map { it.roomName })
    }

    @Test
    fun `sorting by room uses number-aware names`() {
        val rooms =
            listOf(option("F46", "19:00"), option("206", "20:00"), option("103A", "21:00"), option("F9", "18:00"))
                .groupByRoom(sortByTime = false)

        assertEquals(listOf("103A", "206", "F9", "F46"), rooms.map { it.roomName })
    }

    @Test
    fun `each room's windows are in time order`() {
        val room = listOf(option("L15", "21:00"), option("L15", "19:00"), option("L15", "20:00")).groupByRoom(false).single()

        assertEquals(listOf(19, 20, 21), room.options.map { it.start.hour })
    }

    @Test
    fun `no options gives no rooms`() {
        assertTrue(emptyList<GsrBookingOption>().groupByRoom(true).isEmpty())
    }

    @Test
    fun `room names compare digit runs as numbers`() {
        val sorted = listOf("F10", "F9", "f2", "PCPSE 206", "PCPSE 103B", "PCPSE 103A", "007", "7").sortedWith(RoomNameComparator)

        assertEquals(listOf("7", "007", "f2", "F9", "F10", "PCPSE 103A", "PCPSE 103B", "PCPSE 206"), sorted)
    }
}
