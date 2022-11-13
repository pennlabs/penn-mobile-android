package com.pennapps.labs.pennmobile.classes

import android.util.Log
import org.joda.time.DateTime
import org.joda.time.IllegalInstantException
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

/**
 * Interval for venues with meal name and Joda Intervals
 * Created by Adel on 7/13/15.
 */
class VenueInterval {
    var date: String? = null

    //@SerializedName("meal")
    var meals: List<MealInterval> = arrayListOf()

    /**
     * Get all the open hour time intervals for this dining hall in a given date
     * @return HashMap of meal name (lunch, dinner) to open hours expressed as a Joda Interval
     */
    val intervals: HashMap<String?, Interval>
        get() {
            val openHours = HashMap<String?, Interval>()
            for (mI in meals) {
                val mealOpenInterval = mI.getInterval(date)
                openHours[mI.type] = mealOpenInterval
            }
            return openHours
        }

    class MealInterval {
        var open: String? = null
        var close: String? = null
        var type: String? = null

        /**
         * Put together the date given with the hours to create a POJO for the time interval in
         * which the dining hall is open.
         * Any time before 6:00am is assumed to be from the next day rather than the given date.
         * @param date Date string in yyyy-MM-dd format
         * @return Time interval in which meal is open represented as a Joda Interval
         */
        fun getInterval(date: String?): Interval {
            var openTime = "$date $open"
            var closeTime = "$date $close"
            // Avoid midnight hour confusion as API returns both 00:00 and 24:00
            // Switch it to more comprehensible 23:59 / 11:59PM
            if (close == "00:00:00Z" || close == "24:00:00Z") {
                closeTime = "$date 23:59:59Z"
            }
            Log.i("VenueInterval", "$openTime")
            if (open == "" && close == "") {
                open ="00:00:00Z"
                close ="00:00:00Z"
                openTime = "$date $open"
                closeTime = "$date $close"
            }
            val openInstant = DateTime.parse(openTime, dateFormat)
            var closeInstant: DateTime
            try {
                closeInstant = DateTime.parse(closeTime, dateFormat)
            } catch (e: IllegalInstantException) {
                closeTime = "$date 01:00:00Z"
                closeInstant = DateTime.parse(closeTime, dateFormat)
            }

            // Close hours sometimes given in AM hours of next day
            // Cutoff for "early morning" hours was decided to be 6AM
            if (closeInstant.hourOfDay < 6) {
                closeInstant = closeInstant.plusDays(1)
            }
            try {
                return Interval(openInstant, closeInstant)
            } catch (e: Exception) {
                Log.e("VenueInterval", "Error creating interval", e)
                closeInstant = closeInstant.plusHours(24)
                return Interval(openInstant, closeInstant)
            }
        }

        fun getFormattedHour(hours: String): String {
            try {
                var newHours = hours.substring(0, 5)
                val hour = hours.substring(0, 2).toInt()
                if (hour > 12) {
                    newHours = "" + (hour - 12) + hours.substring(2, 5)
                }
                newHours += if (hour >= 12) {
                    "pm"
                } else {
                    "am"
                }
                return newHours

            } catch (exception: Exception) {
                Log.d("Time Formatting Error", exception.message ?: "")
                return hours
            }

        }

        fun getFormattedHourZ(hours: String): String {
            try {
                var newHours = hours.substring(0, 5)
                var hour = hours.substring(0, 2).toInt() - 5
                if(hour < 0) {
                    hour += 24
                }
                if (hour > 12) {
                    newHours = "" + (hour - 12) + hours.substring(2, 5)
                } else {
                    newHours = "" + hour + hours.substring(2, 5)
                }
                newHours += if (hour >= 12) {
                    "pm"
                } else {
                    "am"
                }
                return newHours

            } catch (exception: Exception) {
                Log.d("Time Formatting Error", exception.message ?: "")
                return hours
            }

        }

        companion object {
            /**
             * Date format used by dining API.
             * Example: "2015-08-10 15:00:00"
             */
            val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ")
        }
    }
}