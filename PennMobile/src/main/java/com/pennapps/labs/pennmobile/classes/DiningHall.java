package com.pennapps.labs.pennmobile.classes;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DiningHall {

    private int id;
    private String name;
    // Refers to whether the dining hall is residential or retail
    private boolean residential;
    private boolean hasMenu;
    public HashMap<String, Interval> openHours;
    private HashMap<String, String> dinnerMenu;
    private HashMap<String, String> lunchMenu;

    public DiningHall(int id, String name, boolean residential, boolean hasMenu, JSONArray hours) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.hasMenu = hasMenu;
        this.openHours = parseHours(hours);
    }

    private HashMap<String, Interval> parseHours(JSONArray hours) {
        HashMap<String, Interval> openHours = new HashMap<String, Interval>();
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        try {
            JSONObject hoursToday = hours.getJSONObject(0);
            String date = hoursToday.getString("date");

            JSONArray meals = hoursToday.getJSONArray("meal");
            for (int i = 0; i < meals.length(); i++) {
                JSONObject meal = meals.getJSONObject(i);
                String mealName = meal.getString("type");

                String openTime = date + " " + meal.getString("open");
                String closeTime = date + " " + meal.getString("close");
                if (meal.getString("close").equals("00:00:00")) {
                    closeTime = date + " " + "23:59:59";
                }
                DateTime openInstant = DateTime.parse(openTime, dateFormat);
                DateTime closeInstant = DateTime.parse(closeTime, dateFormat);

                Interval mealOpenInterval = new Interval(openInstant, closeInstant);
                openHours.put(mealName, mealOpenInterval);
            }
        } catch (JSONException e) {

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

    public void setDinnerMenu(HashMap<String, String> dinnerMenu) {
        this.dinnerMenu = dinnerMenu;
    }

    public void setLunchMenu(HashMap<String, String> lunchMenu) {
        this.lunchMenu = lunchMenu;
    }

    public HashMap<String, String> getDinnerMenu() {
        return dinnerMenu;
    }

    public HashMap<String, String> getLunchMenu() {
        return lunchMenu;
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

    public class Meal {
        public String name;
        public HashMap<String, String> menu;
    }
}
