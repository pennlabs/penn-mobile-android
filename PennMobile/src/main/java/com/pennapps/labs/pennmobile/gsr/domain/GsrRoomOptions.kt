package com.pennapps.labs.pennmobile.gsr.domain

import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import java.time.Instant

/** One room's bookable windows, earliest first. */
data class GsrRoomOptions(
    val roomId: Int,
    val roomName: String,
    val options: List<GsrBookingOption>,
) {
    val earliestStart: Instant get() = options.first().start.toInstant()
}

/**
 * Groups search results into one row per room.
 *
 * Sorting by time puts the room that frees up soonest first, so when everything is taken
 * right now the user still sees the earliest opening at the top. Sorting by room uses
 * number-aware name order (103A, 206, F46).
 */
fun List<GsrBookingOption>.groupByRoom(sortByTime: Boolean): List<GsrRoomOptions> {
    val rooms =
        groupBy { it.roomId }
            .map { (roomId, options) ->
                val sorted = options.sortedBy { it.start.toInstant() }
                GsrRoomOptions(roomId, sorted.first().roomName, sorted)
            }
    val byName = compareBy(RoomNameComparator) { room: GsrRoomOptions -> room.roomName }
    val byEarliest = compareBy<GsrRoomOptions> { it.earliestStart }
    return rooms.sortedWith(if (sortByTime) byEarliest.then(byName) else byName)
}

/** Compares runs of digits as numbers so "F9" sorts before "F10". */
internal object RoomNameComparator : Comparator<String> {
    private val chunks = Regex("\\d+|\\D+")

    override fun compare(
        a: String,
        b: String,
    ): Int {
        val left = chunks.findAll(a).map { it.value }.toList()
        val right = chunks.findAll(b).map { it.value }.toList()
        for (i in 0 until minOf(left.size, right.size)) {
            val x = left[i]
            val y = right[i]
            val result =
                if (x[0].isDigit() && y[0].isDigit()) {
                    x.toBigInteger().compareTo(y.toBigInteger()).takeIf { it != 0 } ?: x.length.compareTo(y.length)
                } else {
                    x.compareTo(y, ignoreCase = true)
                }
            if (result != 0) return result
        }
        return left.size.compareTo(right.size)
    }
}
