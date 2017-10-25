package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jackie on 2017-10-22.
 */

public class Machine {

    @SerializedName("offline")
    @Expose
    private Integer offline;
    @SerializedName("open")
    @Expose
    private Integer open;
    @SerializedName("out_of_order")
    @Expose
    private Integer outOfOrder;
    @SerializedName("running")
    @Expose
    private Integer running;
    @SerializedName("time_remaining")
    @Expose
    private List<Integer> timeRemaining = null;

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getOutOfOrder() {
        return outOfOrder;
    }

    public void setOutOfOrder(Integer outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public List<Integer> getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(List<Integer> timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
