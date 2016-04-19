package com.pennapps.labs.pennmobile.classes;

import android.graphics.Color;
import android.support.annotation.AnyRes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Class for bus routes for a given route.
 * Created by Jason on 4/22/2015.
 */
public class BusRoute {
    public ArrayList<BusStop> stops;
    public double walkingDistanceAfter;
    public double walkingDistanceBefore;
    public String route_name;
    public PolylineOptions polylineOptions;
    public int color;

    public BusRoute() {
        stops = new ArrayList<>();
    }

    /**
     * Provide appropriate colors for each bus route.
     * @return Color as an int, output of `Color.rgb`
     */
    @AnyRes
    public int getColor() {
        switch (route_name) {
            case "Campus Loop":
                return Color.rgb(76, 175, 80);
            case "PennBUS West":
                return Color.rgb(244, 67, 54);
            case "PennBUS East":
                return Color.rgb(63, 81, 181);
        }
        return Color.GRAY;
    }

    public PolylineOptions getPolylineOptions() {
        if (polylineOptions != null) {
            return polylineOptions;
        }
        polylineOptions = new PolylineOptions();
        for (BusStop busStop : stops) {
            for (BusStop bs : busStop.path_to) {
                polylineOptions.add(bs.getLatLng());
            }
            polylineOptions.add(busStop.getLatLng());
        }

        for (BusStop bs : stops.get(0).path_to) {
            polylineOptions.add(bs.getLatLng());
        }

        LatLng latLngBuff = stops.get(0).getLatLng();
        polylineOptions.add(latLngBuff);
        polylineOptions.width(15).color(getColor());
        return polylineOptions;
    }

    public void setStops(ArrayList<BusStop> stops) {
        this.stops = stops;
    }
}
