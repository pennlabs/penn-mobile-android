package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Machines {

    @SerializedName("dryers")
    @Expose
    private MachineList dryers;
    @SerializedName("washers")
    @Expose
    private MachineList washers;

    public MachineList getDryers() {
        return dryers;
    }

    public MachineList getWashers() {
        return washers;
    }
}