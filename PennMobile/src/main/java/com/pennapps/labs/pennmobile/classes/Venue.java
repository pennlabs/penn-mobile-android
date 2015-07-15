package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;
import com.pennapps.labs.pennmobile.api.Labs;

import org.joda.time.DateTime;
import org.joda.time.Interval;

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

    public boolean hasMenu(Labs mAPI) {
        try {
            return mAPI.daily_menu(id).menus.size() > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    public HashMap<String, Interval> getHours() {
        if (hours.isEmpty()) {
            return new HashMap<>();
        }
        DateTime currentTime = new DateTime();
        VenueInterval hoursToday = hours.get(0);
        int dayOfWeek = 0;
        // Split by T gets the Y-M-D format to compare against the date in JSON
        while (!hoursToday.date.equals(currentTime.toString().split("T")[0])) {
            hoursToday = hours.get(dayOfWeek);
            dayOfWeek++;
        }

        return hoursToday.getIntervals();
    }
}
