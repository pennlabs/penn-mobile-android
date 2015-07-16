package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiningHall implements Parcelable {

    private int id;
    private String name;
    // Refers to whether the dining hall is residential or retail
    private boolean residential;
    private boolean hasMenu;
    private HashMap<String, Interval> openHours;
    public LinkedHashMap<String, HashMap<String, HashSet<String>>> menus;

    public DiningHall(int id, String name, boolean residential, boolean hasMenu, HashMap<String, Interval> hours) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.hasMenu = hasMenu;
        this.openHours = hours;
        this.menus = new LinkedHashMap<>();
    }

    protected DiningHall(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<DiningHall> CREATOR = new Creator<DiningHall>() {
        @Override
        public DiningHall createFromParcel(Parcel in) {
            return new DiningHall(in);
        }

        @Override
        public DiningHall[] newArray(int size) {
            return new DiningHall[size];
        }
    };

    public void parseMeals(NewDiningHall h) {
        for (NewDiningHall.Menu menu : h.menus) {
            this.menus.put(menu.name, menu.getStationMap());
        }
    }

    public void parseMeal(JSONObject meal) {
        try {
            String mealName = meal.getString("txtDayPartDescription");

            JSONArray stations = new JSONArray();
            try {
                stations = meal.getJSONArray("tblStation");
            } catch (JSONException e) {
                JSONObject stationsObject = meal.getJSONObject("tblStation");
                stations.put(stationsObject);
            }
            HashMap<String, HashSet<String>> currentMenu = new HashMap<>();
            for (int j = 0; j < stations.length(); j++) {
                JSONObject station = stations.getJSONObject(j);
                parseStation(station, currentMenu);
            }

            if (mealName != null) {
                this.menus.put(mealName, currentMenu);
            }
        } catch (JSONException ignored) {

        }
    }

    public void parseStation(JSONObject station, HashMap<String, HashSet<String>> menu) {
        try {
            String stationName = station.getString("txtStationDescription");
            JSONArray stationItems = new JSONArray();
            try {
                stationItems = station.getJSONArray("tblItem");
            } catch (JSONException e) {
                JSONObject stationItem = station.getJSONObject("tblItem");
                stationItems.put(stationItem);
            }
            for (int k = 0; k < stationItems.length(); k++) {
                JSONObject foodItem = stationItems.getJSONObject(k);
                String foodName = foodItem.getString("txtTitle");
                foodName = StringEscapeUtils.unescapeHtml4(foodName);
                if (menu.containsKey(stationName)) {
                    HashSet<String> items = menu.get(stationName);
                    items.add(foodName);
                    menu.put(stationName, items);
                } else {
                    HashSet<String> items = new HashSet<>();
                    items.add(foodName);
                    menu.put(stationName, items);
                }
            }
        } catch (JSONException ignored) {

        }
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[] {residential, hasMenu});
        dest.writeMap(openHours);
        dest.writeMap(menus);
        dest.writeInt(id);
        dest.writeString(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isResidential() {
        return residential;
    }
    public boolean hasMenu() {
        return hasMenu;
    }

    public String closingTime() {
        String closingTime = "";
        for (Interval openInterval : openHours.values()) {
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                closingTime = openInterval.getEnd().toString("h:mma");
                return closingTime;
            }
        }
        return closingTime;
    }

    public String openingTime() {
        List<Map.Entry<String, Interval>> list = new ArrayList<>(openHours.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<String, Interval>>() {
            public int compare( Map.Entry<String, Interval> x, Map.Entry<String, Interval> y )
            {
                return x.getValue().getStart().compareTo(y.getValue().getStart());
            }
        });

        String openingTime = "";

        for (int i = 0; i < list.size(); i++) {
            Interval openInterval = list.get(i).getValue();
            if (openInterval.isAfterNow()) {
                openingTime = openInterval.getStart().toString("h:mma");
                return openingTime;
            }
        }

        return openingTime;
    }

    public boolean isOpen() {
        for (Interval openInterval : openHours.values()) {
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                return true;
            }
        }
        return false;
    }

    public String openMeal() {
        for (Map.Entry<String, Interval> entry : openHours.entrySet()) {
            Interval openInterval = entry.getValue();
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String nextMeal() {
        List<Map.Entry<String, Interval>> list = new ArrayList<>(openHours.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<String, Interval>>() {
            public int compare( Map.Entry<String, Interval> x, Map.Entry<String, Interval> y )
            {
                return x.getValue().getStart().compareTo(y.getValue().getStart());
            }
        });

        String nextMeal = "";

        for (int i = 0; i < list.size(); i++) {
            Interval openInterval = list.get(i).getValue();
            if (openInterval.isAfterNow()) {
                nextMeal = list.get(i).getKey();
                return nextMeal;
            }
        }

        return nextMeal;
    }
}
