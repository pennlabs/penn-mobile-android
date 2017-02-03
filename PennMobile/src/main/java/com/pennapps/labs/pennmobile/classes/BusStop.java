package com.pennapps.labs.pennmobile.classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;


public class BusStop {

    @SerializedName("BusStopId") private String id;
    @SerializedName("BusStopName") private String name;
    @SerializedName("Latitude") private double latitude;
    @SerializedName("Longitude") private double longitude;
    public Object routes;
    public Map<String, Integer> routesMap;
    public int order;
    public List<BusStop> path_to;
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

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusStop busStop = (BusStop) o;

        if (Double.compare(busStop.latitude, latitude) != 0) return false;
        if (Double.compare(busStop.longitude, longitude) != 0) return false;
        if (order != busStop.order) return false;
        if (id != null ? !id.equals(busStop.id) : busStop.id != null) return false;
        if (name != null ? !name.equals(busStop.name) : busStop.name != null) return false;
        if (routes != null ? !routes.equals(busStop.routes) : busStop.routes != null) return false;
        return !(path_to != null ? !path_to.equals(busStop.path_to) : busStop.path_to != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (routes != null ? routes.hashCode() : 0);
        result = 31 * result + order;
        result = 31 * result + (path_to != null ? path_to.hashCode() : 0);
        return result;
    }
}
