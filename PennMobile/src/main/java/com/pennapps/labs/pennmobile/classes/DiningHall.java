package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
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
    private HashMap<String, Interval> openHours;
    public List<NewDiningHall.Menu> menus;

    public DiningHall(int id, String name, boolean residential, HashMap<String, Interval> hours) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.openHours = hours;
        this.menus = new ArrayList<>();
    }

    protected DiningHall(Parcel in) {
        boolean[] booleanArray = new boolean[1];
        in.readBooleanArray(booleanArray);
        residential = booleanArray[0];
        openHours = new HashMap<>();
        menus = new ArrayList<>();
        in.readMap(openHours, Interval.class.getClassLoader());
        in.readList(menus, ArrayList.class.getClassLoader());
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

    public void setMeals(NewDiningHall h) {
        this.menus = h.menus;
        String[] meals = {"Breakfast", "Brunch", "Lunch", "Dinner", "Express"};
        final List<String> mealOrder = Arrays.asList(meals);
        Collections.sort(this.menus, new Comparator<NewDiningHall.Menu>() {
            @Override
            public int compare(NewDiningHall.Menu lhs, NewDiningHall.Menu rhs) {
                return mealOrder.indexOf(lhs.name) - mealOrder.indexOf(rhs.name);
            }
        });
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[] {residential});
        dest.writeMap(openHours);
        dest.writeList(menus);
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
        return menus.size() > 0;
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
