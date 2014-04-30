package com.pennapps.labs.pennmobile;

public class BusStopDist implements Comparable<BusStopDist> {

    private String name;
    private double distance;

    public BusStopDist(String name, double distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(BusStopDist another) {
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
