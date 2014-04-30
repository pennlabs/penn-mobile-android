package com.pennapps.labs.pennmobile;

import java.util.ArrayList;

public class BusRoute {

    // private String direction;
    private String description;
    private String title;
    private ArrayList<BusRouteStop> routeStops;

    public BusRoute(String title, String description) {
        this.description = description;
        this.title = title;
    }

    public BusRoute(String title, String description, ArrayList<BusRouteStop> routeStops) {
        // this.direction = direction;
        this.description = description;
        this.title = title;
        this.routeStops = routeStops;
    }

    public ArrayList<BusRouteStop> getRouteStops() {
        return routeStops;
    }

    public String getTitle() {
        return title;
    }
}
