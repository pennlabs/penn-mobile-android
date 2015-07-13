package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;

/**
 * Interval for venues with meal name and Joda Intervals
 * Created by Adel on 7/13/15.
 */
public class VenueInterval {
    public String date;
    @SerializedName("meal") public List<MealInterval> meals;

    public HashMap<String, Interval> getIntervals() {
        HashMap<String, Interval> openHours = new HashMap<>();
        for (MealInterval mI : meals) {
            Interval mealOpenInterval = mI.getInterval(date);
            openHours.put(mI.type, mealOpenInterval);
        }
        return openHours;
    }

    public static class MealInterval {
        public String open;
        public String close;
        public String type;

        static final DateTimeFormatter DATEFORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        public Interval getInterval(String date) {
            String openTime = date + " " + open;
            String closeTime = date + " " + close;
            if (close.equals("00:00:00") || close.equals("24:00:00")) {
                closeTime = date + " " + "23:59:59";
            }
            DateTime openInstant = DateTime.parse(openTime, DATEFORMAT);
            DateTime closeInstant = DateTime.parse(closeTime, DATEFORMAT);

            if (closeInstant.getHourOfDay() < 6) {
                closeInstant = closeInstant.plusDays(1);
            }

            return new Interval(openInstant, closeInstant);
        }
    }
}
