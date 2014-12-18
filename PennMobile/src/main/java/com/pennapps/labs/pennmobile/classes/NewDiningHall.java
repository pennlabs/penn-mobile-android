package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 12/18/14.
 * Class for Dining Halls with Retrofit
 * Should replace DiningHall class at some point
 */
public class NewDiningHall {
    public String name;
    @SerializedName("tblDayPart") public List<Menu> menus = new ArrayList<>();
}
