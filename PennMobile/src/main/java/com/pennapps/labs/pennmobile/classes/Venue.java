package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Adel on 12/16/14.
 * Class for Dining Venues from Business Services API
 */
public class Venue {
    public int id;
    public String name;
    public String venueType;
    @SerializedName("dateHours") public List<VenueInterval> hours = new ArrayList<>();

    /**
     * Indicates whether a dining hall is residential (as opposed to retail).
     * @return boolean of whether a dining hall is labeled as residential
     */
    public boolean isResidential() {
        return venueType.equals("residential") && !name.equals("Cafe at McClelland");
    }

    /**
     * Get a mapping of meal names to open hours for all meals in the dining hall this week
     * @return HashMap of meal name (lunch, dinner) to open hours expressed as a Joda Interval
     */
    public HashMap<String, Interval> getHours() {
        DateTime currentTime = new DateTime();
        DateTimeFormatter intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime intervalDateTime;
        VenueInterval today = new VenueInterval();
        VenueInterval tomorrow = new VenueInterval();
        for (VenueInterval interval : hours) {
            intervalDateTime = intervalFormatter.parseDateTime(interval.date);
            if (intervalDateTime.toLocalDate().equals(currentTime.toLocalDate())) {
                today = interval;
            } else if(intervalDateTime.toLocalDate().equals(currentTime.plusDays(1).toLocalDate())) {
                tomorrow = interval;
            }
        }

        return VenueInterval.getIntervals(today, tomorrow);
    }
}
