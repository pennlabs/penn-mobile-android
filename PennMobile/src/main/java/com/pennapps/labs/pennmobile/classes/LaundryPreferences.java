package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Andrew on 3/6/2018.
 */

public class LaundryPreferences {
    @SerializedName("rooms")
    @Expose
    private List<Integer> rooms;

    public LaundryPreferences(List<Integer> rooms) {
        this.rooms = rooms;
    }
}
