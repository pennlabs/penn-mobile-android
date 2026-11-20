package com.pennapps.labs.pennmobile.gsr.repo

import android.util.Log
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation
import com.pennapps.labs.pennmobile.gsr.classes.GSRRoom
import com.pennapps.labs.pennmobile.gsr.classes.GSRSlot
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrSlot
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val TAG = "GsrMappers"

/**
 * Format the backend hands straight to LibCal/Wharton when booking (`%Y-%m-%dT%H:%M:%S%z`).
 * Not ISO_OFFSET_DATE_TIME, which drops `:00` seconds and would change the strings we send.
 */
private val BOOKING_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")

internal fun parseGsrTime(value: String?): OffsetDateTime? =
    value?.let {
        try {
            OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (e: DateTimeParseException) {
            null
        }
    }

internal fun formatGsrTime(time: OffsetDateTime): String = BOOKING_TIME_FORMAT.format(time)

fun GsrBookingOption.startTimeParam(): String = formatGsrTime(start)

fun GsrBookingOption.endTimeParam(): String = formatGsrTime(end)

internal fun GSRLocation.toDomain(): GsrLocation? {
    val lid = id?.takeIf { it.isNotBlank() } ?: return null
    val name = name?.takeIf { it.isNotBlank() } ?: return null
    return GsrLocation(
        lid = lid,
        gid = gid,
        name = name,
        kind = kind.toLocationKind(),
        bookableDays = bookableDays,
    )
}

private fun String?.toLocationKind(): GsrLocation.Kind =
    GsrLocation.Kind.entries.firstOrNull { it.name == this && it != GsrLocation.Kind.UNKNOWN }
        ?: GsrLocation.Kind.UNKNOWN

internal fun GSRRoom.toDomain(): GsrRoom? {
    val roomId = roomId
    val name = name
    if (roomId == null || name.isNullOrBlank()) {
        Log.w(TAG, "Skipping room with missing id or name: id=$roomId name=$name")
        return null
    }
    return GsrRoom(
        roomId = roomId,
        name = name,
        gid = gid ?: 0,
        capacity = capacity,
        thumbnailUrl = thumbnail,
        slots = slots.orEmpty().mapNotNull { it.toDomain() },
    )
}

/** LibCal omits `available` and only returns free slots, so a missing flag means available. */
internal fun GSRSlot.toDomain(): GsrSlot? {
    val start = parseGsrTime(startTime)
    val end = parseGsrTime(endTime)
    if (start == null || end == null) {
        Log.w(TAG, "Skipping slot with unparseable times: $startTime - $endTime")
        return null
    }
    return GsrSlot(start = start, end = end, isAvailable = isAvailable)
}
