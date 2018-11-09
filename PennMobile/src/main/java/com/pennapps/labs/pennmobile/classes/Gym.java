package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.LinkedList;
import java.util.List;

public class Gym {
    // fields from parsing JSON
    @SerializedName("hours")
    @Expose
    private List<GymHours> hoursList;
    @Expose
    private String name;

    // other fields
    private List<Interval> intervalList;

    public String getName() {
        return name;
    }

    @NotNull
    public List<Interval> getHours() {
        // check if we've already gotten the hours. if we have, just return it, if not, get it
        if (intervalList != null) {
            return intervalList;
        }
        intervalList = new LinkedList();
        for (GymHours g: hoursList) {
            intervalList.add(g.getInterval());
        }
        return intervalList;
    }

    public boolean isOpen() {
        // fill intervalList
        if (intervalList == null) {
            getHours();
        }
        DateTime current = new DateTime();
        for (Interval i: intervalList) {
            if (i.contains(current)) {
                return true;
            }
        }
        return false;
    }

    public Interval currentInterval() {

        DateTime current = new DateTime();
        for (Interval i: intervalList) {
            if (i.contains(current)) {
                return i;
            }
        }

        // if this isn't open right now, throw exception
        throw new IllegalStateException();
    }
}
