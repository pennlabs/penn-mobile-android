package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Machines {

    @SerializedName("dryers")
    @Expose
    private MachineList dryers;
    @SerializedName("washers")
    @Expose
    private MachineList washers;
    @SerializedName("details")
    @Expose
    private List<MachineDetail> machineDetailList;

    public MachineList getDryers() {
        return dryers;
    }

    public MachineList getWashers() {
        return washers;
    }

    public List<MachineDetail> getMachineDetailList() {
        return machineDetailList;
    }
}