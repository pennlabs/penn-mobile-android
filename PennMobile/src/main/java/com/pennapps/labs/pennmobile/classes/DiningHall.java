package com.pennapps.labs.pennmobile.classes;

import java.util.HashMap;

public class DiningHall {

    private int id;
    private String name;
    // Refers to whether the dining hall is residential or retail
    private boolean residential;
    private HashMap<String, String> dinnerMenu;
    private HashMap<String, String> lunchMenu;

    public DiningHall(int id, String name, boolean residential) {
        this.id = id;
        this.name = name;
        this.residential = residential;
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

    public class Meal {
        public String name;
        public HashMap<String, String> menu;
    }
}
