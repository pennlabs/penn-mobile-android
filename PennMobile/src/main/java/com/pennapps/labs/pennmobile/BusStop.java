package com.pennapps.labs.pennmobile;

public class BusStop implements Comparable<BusStop> {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double distance;

    public BusStop(String name, double distance) {
        this.name = name;
        this.distance = distance;
    }

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

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(BusStop another) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == another) return EQUAL;
        if (this.getDistance() < another.getDistance()) {
            return BEFORE;
        } else {
            return AFTER;
        }
    }
}
