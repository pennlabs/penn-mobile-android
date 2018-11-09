package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GymHours {

    public static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    public static final Interval allDayInterval = new Interval(0,0);


    @SerializedName("all_day")
    private boolean allDay;

    @Expose
    private String start;
    @Expose
    private String end;

    public Interval getInterval() {
        if (allDay) {
            return allDayInterval;
        }
        DateTime startDateTime = formatter.parseDateTime(start);
        DateTime endDateTime = formatter.parseDateTime(end);
        return new Interval(startDateTime, endDateTime);
    }

}
