package com.pennapps.labs.pennmobile;

public class BusStop {

    private String id;
    private String name;
    private double latitude;
    private double longitude;

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
