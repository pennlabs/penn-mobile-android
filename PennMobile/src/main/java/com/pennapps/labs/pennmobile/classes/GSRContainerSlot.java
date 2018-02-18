package com.pennapps.labs.pennmobile.classes;

/**
 * Created by MikeD on 2/10/2018.
 */

public class GSRContainerSlot {

    private String timeRange;
    private String dateTime;
    private String dayDate;
    private String dateNum;
    private String duration;
    private String elementId;



    public GSRContainerSlot (String constructorTimeRange, String constructorDateTime,
                    String constructorDayDate, String constructorDateNum, String constructorDuration, String constructorElementId) {

        timeRange = constructorTimeRange;
        dateTime = constructorDateTime;
        dayDate = constructorDayDate;
        dateNum = constructorDateNum;
        duration = constructorDuration;
        elementId = constructorElementId;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDayDate() {
        return dayDate;
    }

    public String getDateNum() {
        return dateNum;
    }

    public String getDuration() {
        return duration;
    }

    public String getElementId() {
        return elementId;
    }
}