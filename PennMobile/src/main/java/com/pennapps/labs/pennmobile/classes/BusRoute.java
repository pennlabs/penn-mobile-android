package com.pennapps.labs.pennmobile.classes;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.annotations.SerializedName;
import com.pennapps.labs.pennmobile.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for bus routes for a given route.
 * Created by Jason on 4/22/2015.
 */
public class BusRoute {
    @SerializedName("path") public ArrayList<BusStop> stops;
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
                polylineOptions.add(new LatLng(bs.getLatitude(), bs.getLongitude()));
            }
            LatLng latLngBuff = new LatLng(busStop.getLatitude(), busStop.getLongitude());
            if (busStop.getName() != null) {
                markers.add(new MarkerOptions()
                        .position(latLngBuff)
                        .title(busStop.getName())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_brightness_1_black_18dp)));
            }
            polylineOptions.add(latLngBuff);
        }

        for(BusStop bs: stops.get(0).path_to){
            polylineOptions.add(new LatLng(bs.getLatitude(), bs.getLongitude()));
        }
        LatLng latLngBuff = new LatLng(stops.get(0).getLatitude(),stops.get(0).getLongitude());
        polylineOptions.add(latLngBuff);
        for (BusStop bs : stops.get(0).path_to) {
            polylineOptions.add(new LatLng(bs.getLatitude(), bs.getLongitude()));
        }
        polylineOptions.width(15).color(getColor());
        return polylineOptions;
    }
}
