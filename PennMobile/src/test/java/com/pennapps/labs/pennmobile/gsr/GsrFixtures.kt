package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.domain.PENN_ZONE
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrSlot
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

val DAY: LocalDate = LocalDate.of(2026, 10, 9)

val VP_GROUND_FLOOR = GsrLocation("1086", 4660, "VP Ground Floor", GsrLocation.Kind.LIBCAL, 7)
val HUNTSMAN = GsrLocation("JMHH", 1, "Huntsman", GsrLocation.Kind.WHARTON, 7)
val BIOTECH = GsrLocation("2683", 1885, "Biotech Commons", GsrLocation.Kind.LIBCAL, 7)
val AMY_GUTMANN = GsrLocation("AGH", 7, "Amy Gutmann Hall", GsrLocation.Kind.PENNGRP, 7)

fun at(
    time: String,
    day: LocalDate = DAY,
): ZonedDateTime = ZonedDateTime.of(day, LocalTime.parse(time), PENN_ZONE)

fun offsetAt(
    time: String,
    day: LocalDate = DAY,
): OffsetDateTime = at(time, day).toOffsetDateTime()

/** A 30-minute slot starting at [time] (HH:mm) on [day]. */
fun slot(
    time: String,
    day: LocalDate = DAY,
    available: Boolean = true,
): GsrSlot {
    val start = offsetAt(time, day)
    return GsrSlot(start, at(time, day).plusMinutes(30).toOffsetDateTime(), available)
}

fun room(
    name: String,
    vararg slots: GsrSlot,
    id: Int = name.hashCode(),
): GsrRoom = GsrRoom(id, name, gid = 999, capacity = 4, thumbnailUrl = null, slots = slots.toList())
