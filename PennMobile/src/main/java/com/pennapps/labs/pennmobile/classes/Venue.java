package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adel on 12/16/14.
 * Class for Dining Venues from Business Services API
 */
public class Venue implements Serializable {
    public int id;
    public String name;
    public String venueType;
    public ArrayList<String> extras;
    @SerializedName("dateHours")
    public List<VenueInterval> hours = new ArrayList<>();

    /**
     * Indicates whether a dining hall is residential (as opposed to retail).
     *
     * @return boolean of whether a dining hall is labeled as residential
     */
    public boolean isResidential() {
        return venueType.equals("residential") && !name.equals("Cafe at McClelland");
    }

    /**
     * Get a mapping of meal names to open hours for all meals in the dining hall this week
     *
     * @return HashMap of meal name (lunch, dinner) to open hours expressed as a Joda Interval
     */
    public HashMap<String, Interval> getHours() {
        DateTime currentTime = new DateTime();
        // Split by T gets the Y-M-D format to compare against the date in JSON
        DateTime tomorrow = currentTime.plusDays(1);
        DateTimeFormatter intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime intervalDateTime;
        HashMap<String, Interval> intervals = new HashMap<>();
        for (VenueInterval interval : hours) {
            intervalDateTime = intervalFormatter.parseDateTime(interval.date);
            if (intervalDateTime.toLocalDate().equals(tomorrow.toLocalDate())) {
                intervals.putAll(interval.getIntervals());
            }
        }
        for (VenueInterval interval : hours) {
            intervalDateTime = intervalFormatter.parseDateTime(interval.date);
            if (intervalDateTime.toLocalDate().equals(currentTime.toLocalDate())) {
                for (Map.Entry<String, Interval> entry : interval.getIntervals().entrySet()) {
                    if (entry.getValue().contains(currentTime) ||
                            currentTime.isBefore(entry.getValue().getStart())) {
                        intervals.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return intervals;
    }

    public List<VenueInterval> allHours(){
        return hours;
    }

    public List<String> getExtras(){
        return extras;
    }
}
