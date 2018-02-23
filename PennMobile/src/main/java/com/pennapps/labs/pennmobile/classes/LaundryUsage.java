package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jackie on 2017-12-26.
 */

public class LaundryUsage {
    @SerializedName("day_of_week")
    @Expose
    private String dayOfWeek;
    @SerializedName("dryer_data")
    @Expose
    private MachineData dryerData;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("hall_name")
    @Expose
    private String hallName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("total_number_of_dryers")
    @Expose
    private Double totalNumberOfDryers;
    @SerializedName("total_number_of_washers")
    @Expose
    private Double totalNumberOfWashers;
    @SerializedName("washer_data")
    @Expose
    private MachineData washerData;
    private int id;

    public void setDryerData() {
        dryerData = new MachineData();
        dryerData.setData();
    }

    public MachineData getDryerData() {
        return dryerData;
    }

    public void setWasherData() {
        washerData = new MachineData();
        washerData.setData();
    }

    public MachineData getWasherData() {
        return washerData;
    }

    public String getHallName() {
        return hallName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
