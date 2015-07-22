package com.pennapps.labs.pennmobile.classes;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pennapps.labs.pennmobile.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public Set<MarkerOptions> markers;
    public int color;

    public BusRoute() {
        stops = new ArrayList<>();
        markers = new HashSet<>();
    }

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
            LatLng latLngBuff = busStop.getLatLng();
            if (busStop.getName() != null) {
                markers.add(new MarkerOptions()
                        .position(latLngBuff)
                        .title(busStop.getName())
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_brightness_1_black_18dp)));
            }
            polylineOptions.add(latLngBuff);
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
