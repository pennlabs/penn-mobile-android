package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GymHours {

    @SerializedName("all_day")
    private var allDay: Boolean = false

    @Expose
    private var start: String? = null
    @Expose
    private var end: String? = null

    // Secondary constructor used for testing
    constructor(_allDay: Boolean, _start: String?, _end: String?) {
        allDay = _allDay
        start = _start
        end = _end
    }

    val interval: Interval
        get() {
            if (allDay) {
                return allDayInterval
            }
            val startDateTime = formatter.parseDateTime(start)
            val endDateTime = formatter.parseDateTime(end)
            return Interval(startDateTime, endDateTime)
        }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val allDayInterval = Interval(0, 0)
    }

}
