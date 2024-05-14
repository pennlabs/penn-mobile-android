package com.pennapps.labs.pennmobile.classes

import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat

/**
 * Created by Adel on 12/16/14.
 * Class for Dining Venues from Business Services API
 */
class Venue {
    var id = 0
    var name: String? = null
    var venueType: String? = null
    var extras: ArrayList<String>? = null

    // @SerializedName("dateHours")
    var hours: List<VenueInterval> = ArrayList()

    /**
     * Indicates whether a dining hall is residential (as opposed to retail).
     *
     * @return boolean of whether a dining hall is labeled as residential
     */
    val isResidential: Boolean
        get() = venueType == "residential" && name != "Cafe at McClelland"

    /**
     * Get a mapping of meal names to open hours for all meals in the dining hall this week
     *
     * @return HashMap of meal name (lunch, dinner) to today's open hours expressed as a Joda Interval
     */
    fun getHours(): HashMap<String, Interval> {
        val currentTime = DateTime()
        // Split by T gets the Y-M-D format to compare against the date in JSON
        val intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        var intervalDateTime: DateTime
        val intervals = HashMap<String, Interval>()
        for (interval in hours) {
            intervalDateTime = intervalFormatter.parseDateTime(interval.date)
            if (intervalDateTime.toLocalDate() == currentTime.toLocalDate()) {
                for ((key, value) in interval.intervals) {
                    // special cases for McClelland and Houston Market meal times
                    if (key != "Closed" && key != "The Market Café" && key != null) {
                        intervals[key] = value
                    }
                }
            }
        }
        return intervals
    }

    fun allHours(): List<VenueInterval> {
        return hours
    }

    fun getExtras(): List<String>? {
        return extras
    }
}
