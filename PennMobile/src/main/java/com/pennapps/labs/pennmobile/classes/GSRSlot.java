package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MikeD on 10/9/2017.
 */

//class that represents an available session for a given GSR room
public class GSRSlot {

    @SerializedName("available")
    @Expose
    public boolean available;

    @SerializedName("start")
    @Expose
    public String startTime;

    @SerializedName("end")
    @Expose
    public String endTime;


    public boolean isAvailable() {
        return available;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
