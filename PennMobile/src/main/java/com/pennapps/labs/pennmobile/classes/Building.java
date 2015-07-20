package com.pennapps.labs.pennmobile.classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Building {
    public String http_link;
    public String address;
    public String title;
    public String latitude;
    public String longitude;
    @SerializedName("campus_item_images") public List<BuildingImage> images = new ArrayList<>();

    public String getImageURL() {
        if (images.isEmpty()) {
            return "";
        }
        return images.get(0).image_url;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
}
