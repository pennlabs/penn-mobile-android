package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Adel on 12/18/14.
 * Class for Dining Halls with Retrofit
 * Should replace DiningHall class at some point
 */
public class NewDiningHall {
    public String name;
    @SerializedName("tblDayPart") public List<Menu> menus = new ArrayList<>();

    /**
     * Created by Adel on 12/18/14.
     * Class for a single menu, ie. Lunch, Dinner
     */
    public static class Menu {
        @SerializedName("txtDayPartDescription") public String name;
        @SerializedName("tblStation") public List<DiningStation> stations = new ArrayList<>();

        public HashMap<String, HashSet<String>> getStationMap() {
            HashMap<String, HashSet<String>> stationsMap = new HashMap<>();
            for (DiningStation station : stations) {
                stationsMap.put(station.name, station.getItemSet());
            }
            return stationsMap;
        }
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for a station at a dining hall
     */
    public static class DiningStation {
        @SerializedName("txtStationDescription") public String name;
        @SerializedName("tblItem") public List<FoodItem> items = new ArrayList<>();

        public HashSet<String> getItemSet() {
            HashSet<String> itemSet = new HashSet<>();
            for (FoodItem item : items) {
                itemSet.add(item.title);
            }
            return itemSet;
        }
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for Food items in Dining menus
     */
    public static class FoodItem {
        @SerializedName("txtTitle") String title;
        @SerializedName("txtDescription") String description;
    }
}
