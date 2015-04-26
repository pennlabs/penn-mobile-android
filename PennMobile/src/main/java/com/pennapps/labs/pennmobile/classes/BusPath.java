package com.pennapps.labs.pennmobile.classes;

import java.util.ArrayList;
import java.util.List;

public class BusPath {

    public List<BusStop> path = new ArrayList<>();
    public String route_name;
    public double walkingDistanceAfter;
    public double walkingDistanceBefore;

    public BusPath() {
        path = new ArrayList<>();
    }

}
