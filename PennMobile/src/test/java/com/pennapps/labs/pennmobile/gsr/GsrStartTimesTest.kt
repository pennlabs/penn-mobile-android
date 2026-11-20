package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.domain.GsrDayPart
import com.pennapps.labs.pennmobile.gsr.domain.floorToHalfHour
import com.pennapps.labs.pennmobile.gsr.domain.halfHourStartTimes
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class GsrStartTimesTest {
    private fun t(text: String) = LocalTime.parse(text)

    @Test
    fun `times round down to the half-hour already under way`() {
        mapOf("17:00" to "17:00", "17:05" to "17:00", "17:29" to "17:00", "17:30" to "17:30", "17:31" to "17:30", "23:59" to "23:30")
            .forEach { (input, expected) -> assertEquals(input, t(expected), t(input).floorToHalfHour()) }
    }

    @Test
    fun `choices run every half-hour through 11 30 PM`() {
        assertEquals(listOf(t("22:30"), t("23:00"), t("23:30")), halfHourStartTimes(t("22:40")))
        assertEquals(48, halfHourStartTimes(LocalTime.MIDNIGHT).size)
    }

    @Test
    fun `late at night the current half-hour is still offered`() {
        assertEquals(listOf(t("23:30")), halfHourStartTimes(t("23:55")))
    }

    @Test
    fun `day parts split at noon, 5 PM and 9 PM`() {
        mapOf(
            "00:00" to GsrDayPart.MORNING,
            "11:30" to GsrDayPart.MORNING,
            "12:00" to GsrDayPart.AFTERNOON,
            "17:00" to GsrDayPart.AFTERNOON,
            "17:30" to GsrDayPart.EVENING,
            "21:00" to GsrDayPart.EVENING,
            "21:30" to GsrDayPart.NIGHT,
            "23:30" to GsrDayPart.NIGHT,
        ).forEach { (time, part) -> assertEquals(time, part, GsrDayPart.of(t(time))) }
    }
}
