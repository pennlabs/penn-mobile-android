package com.pennapps.labs.pennmobile.gsr.domain

import java.time.LocalTime

/** Rounds down, so the slot already under way still counts: 5:29 → 5:00, 5:31 → 5:30. */
fun LocalTime.floorToHalfHour(): LocalTime = LocalTime.of(hour, if (minute < GSR_SLOT_MINUTES) 0 else GSR_SLOT_MINUTES)

/** Every half-hour from [from] (floored) through 11:30 PM; never empty. */
fun halfHourStartTimes(from: LocalTime): List<LocalTime> =
    generateSequence(from.floorToHalfHour()) { time ->
        time.plusMinutes(GSR_SLOT_MINUTES.toLong()).takeIf { it > time }
    }.toList()

/** Headings the start-time grid groups by; the 5:00/9:00 cut-offs match the design. */
enum class GsrDayPart {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT,
    ;

    companion object {
        fun of(time: LocalTime): GsrDayPart =
            when {
                time < LocalTime.NOON -> MORNING
                time <= LocalTime.of(17, 0) -> AFTERNOON
                time <= LocalTime.of(21, 0) -> EVENING
                else -> NIGHT
            }
    }
}
