package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DiningHall implements Parcelable {

    private int id;
    private String name;
    // Refers to whether the dining hall is residential or retail
    private boolean residential;
    private boolean hasMenu;
    private HashMap<String, Interval> openHours;
    public HashMap<String, HashMap<String, String>> menus;

    DateTimeFormatter DATEFORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public DiningHall(int id, String name, boolean residential, boolean hasMenu, JSONArray hours) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.hasMenu = hasMenu;
        this.openHours = parseHours(hours);
        this.menus = new HashMap<String, HashMap<String, String>>();
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

    private HashMap<String, Interval> parseHours(JSONArray hours) {
        HashMap<String, Interval> openHours = new HashMap<String, Interval>();

        try {

            JSONObject hoursToday = hours.getJSONObject(0);
            String date = hoursToday.getString("date");
            DateTime currentTime = new DateTime();
            int dayOfWeek = 0;
            // Split by T gets the Y-M-D format to compare against the date in JSON
            while (!hoursToday.getString("date").equals(currentTime.toString().split("T")[0])) {
                hoursToday = hours.getJSONObject(dayOfWeek);
                date = hoursToday.getString("date");
                dayOfWeek++;
            }

            try {
                JSONArray meals = hoursToday.getJSONArray("meal");
                for (int i = 0; i < meals.length(); i++) {
                    JSONObject meal = meals.getJSONObject(i);
                    String mealName = meal.getString("type");
                    Interval mealOpenInterval = parseMealHours(meal, date);
                    openHours.put(mealName, mealOpenInterval);
                }
            } catch (JSONException e) {
                JSONObject meal = hoursToday.getJSONObject("meal");
                String mealName = meal.getString("type");
                Interval mealOpenInterval = parseMealHours(meal, date);
                openHours.put(mealName, mealOpenInterval);
            }
        } catch (JSONException ignored) {

        }
        return openHours;
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
    public boolean isRetail() {
        return !residential;
    }
    public boolean hasMenu() {
        return hasMenu;
    }

    public String closingTime() {
        String closingTime = "";
        for (String mealName : openHours.keySet()) {
            Interval openInterval = openHours.get(mealName);
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                closingTime = openInterval.getEnd().toString("h:mma");
                return closingTime;
            }
        }
        return closingTime;
    }

    public String openingTime() {
        List<Map.Entry<String, Interval>> list = new ArrayList<Map.Entry<String, Interval>>(openHours.entrySet());
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
        for (String mealName : openHours.keySet()) {
            Interval openInterval = openHours.get(mealName);
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                return true;
            }
        }
        return false;
    }

    private Interval parseMealHours(JSONObject meal, String date) {
        try {
            String openTime = date + " " + meal.getString("open");
            String closeTime = date + " " + meal.getString("close");
            if (meal.getString("close").equals("00:00:00") ||
                    meal.getString("close").equals("24:00:00")) {
                closeTime = date + " " + "23:59:59";
            }
            DateTime openInstant = DateTime.parse(openTime, DATEFORMAT);
            DateTime closeInstant = DateTime.parse(closeTime, DATEFORMAT);

            if (closeInstant.getHourOfDay() < 6) {
                closeInstant = closeInstant.plusDays(1);
            }

            return new Interval(openInstant, closeInstant);
        } catch (JSONException ignored) {
            return null;
        }
    }

    public String openMeal() {
        for (String mealName : openHours.keySet()) {
            Interval openInterval = openHours.get(mealName);
            DateTime currentTime = new DateTime();
            if (openInterval.contains(currentTime)) {
                return mealName;
            }
        }
        return null;
    }

    public String nextMeal() {
        List<Map.Entry<String, Interval>> list = new ArrayList<Map.Entry<String, Interval>>(openHours.entrySet());
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

    public class Meal {
        public String name;
        public HashMap<String, String> menu;
    }
}
