package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

public class BusStop {

    @SerializedName("BusStopId") private String id;
    @SerializedName("BusStopName") private String name;
    @SerializedName("Latitude") private double latitude;
    @SerializedName("Longitude") private double longitude;

    public BusStop(String id, String name, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
