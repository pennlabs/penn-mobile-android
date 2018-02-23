package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jackie on 2017-12-07.
 */

public class MachineDetail implements Comparable<MachineDetail> {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("time_remaining")
    @Expose
    private int timeRemaining;

    @SerializedName("type")
    @Expose
    private String type;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int time) {
        this.timeRemaining = time;
    }

    public String getType() {
        return type;
    }

    @Override
    public int compareTo(MachineDetail machineDetail) {

        if (timeRemaining == machineDetail.getTimeRemaining()) {
            return 0;
        } else if (timeRemaining > machineDetail.getTimeRemaining()) {
            return 1;
        } else {
            return -1;
        }
    }
}
