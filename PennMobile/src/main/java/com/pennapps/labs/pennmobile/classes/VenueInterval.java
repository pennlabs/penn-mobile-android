package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Interval for venues with meal name and Joda Intervals
 * Created by Adel on 7/13/15.
 */
public class VenueInterval {
    public String date;
    @SerializedName("meal") public List<MealInterval> meals;

    public static class MealInterval {
        public String open;
        public String close;
        public String type;
    }
}
