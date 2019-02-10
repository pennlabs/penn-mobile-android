package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GymHours {


    @SerializedName("all_day")
    private val allDay: Boolean = false

    @Expose
    private val start: String? = null
    @Expose
    private val end: String? = null

    val interval: Interval
        get() {
            if (allDay) {
                return allDayInterval
            }
            val startDateTime = formatter.parseDateTime(start!!)
            val endDateTime = formatter.parseDateTime(end!!)
            return Interval(startDateTime, endDateTime)
        }

    companion object {

        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val allDayInterval = Interval(0, 0)
    }

}
