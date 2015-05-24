package com.pennapps.labs.pennmobile.classes;

import java.util.ArrayList;

/**
 * Class for bus routes for a given route.
 * Created by Jason on 4/22/2015.
 */
public class BusRoute {
    public ArrayList<BusStop> stops;
    public String route_name;

    public BusRoute() {
        stops = new ArrayList<>();
    }
}
