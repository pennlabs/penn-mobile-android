package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MikeD on 10/9/2017.
 */

//class that keeps track of all the GSR rooms themselves
public class GSR {


    @SerializedName("location_id")
    @Expose
    public Integer location_id;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("rooms")
    @Expose
    public GSRRoom[] rooms;


    public Integer getLocation_id() {
        return location_id;
    }

    public String getDate() {
        return date;
    }

    public GSRRoom[] getRooms() {
        return rooms;
    }
}
