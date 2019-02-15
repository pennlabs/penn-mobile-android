package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.util.LinkedList

class Gym {
    // fields from parsing JSON
    @SerializedName("hours")
    @Expose
    private val hoursList: List<GymHours>? = null
    @Expose
    val name: String? = null

    // other fields
    private var intervalList: List<Interval>? = null

    // check if we've already gotten the hours. if we have, just return it, if not, get it
    val hours: List<Interval>?
        get() {
            if (intervalList != null) {
                return intervalList ?: hours
            }
            var tempMutableList: MutableList<Interval> = LinkedList()
            if (hoursList == null) {
                return null
            }
            for (g in hoursList) {
                tempMutableList.add(g.interval)
            }
            intervalList = tempMutableList
            return intervalList
        }

    val isOpen: Boolean
        get() {
            hours // update interval list

            val current = DateTime()
            intervalList?.let { intervals ->
                for (i in intervals) {
                    if (i.contains(current)) {
                        return true
                    }
                }
            }
            return false
        }

    /*fun currentInterval(): Interval {

        val current = DateTime()
        for (i in intervalList!!) {
            if (i.contains(current)) {
                return i
            }
        }

        // if this isn't open right now, throw exception
        throw IllegalStateException()
    }*/
}
