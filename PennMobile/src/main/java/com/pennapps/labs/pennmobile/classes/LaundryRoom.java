package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LaundryRoom {

    private int id;
    @SerializedName("hall_name")
    @Expose
    private String name;
    @SerializedName("machines")
    @Expose
    private Machines machines;

    public Machines getMachines() {
        return machines;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        id = newId;
    }
}
