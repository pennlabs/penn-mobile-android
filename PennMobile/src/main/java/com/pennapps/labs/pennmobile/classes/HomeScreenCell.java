package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jackie on 2018-03-28.
 */

public class HomeScreenCell {
    @SerializedName("info")
    @Expose
    private HomeScreenInfo info;
    @SerializedName("type")
    @Expose
    private String type;

    public HomeScreenInfo getInfo() {
        return info;
    }

    public void setInfo(HomeScreenInfo info) {
        this.info = info;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
