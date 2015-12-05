package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiningHall implements Parcelable {

    private int id;
    private String name;
    // Refers to whether the dining hall is residential or retail
    private boolean residential;
    private HashMap<String, Interval> openHours;
    private Venue venue;
    @SerializedName("tblDayPart") public List<Menu> menus = new ArrayList<>();

    public DiningHall(int id, String name, boolean residential, HashMap<String, Interval> hours, Venue venue) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.openHours = hours;
        this.venue = venue;
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

    public void sortMeals(List<Menu> menus) {
        this.menus = menus;
        String[] meals = {"Breakfast", "Brunch", "Lunch", "Dinner", "Express"};
        final List<String> mealOrder = Arrays.asList(meals);
        Collections.sort(this.menus, new Comparator<Menu>() {
            @Override
            public int compare(Menu lhs, Menu rhs) {
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


    public Venue getVenue(){
        return venue;
    }

    public String closingTime() {
        for (Interval openInterval : openHours.values()) {
            if (openInterval.containsNow()) {
                return openInterval.getEnd().toString("h:mma");
            }
        }
        return "";
    }

    public List<Map.Entry<String, Interval>> orderedHours() {
        List<Map.Entry<String, Interval>> list = new ArrayList<>(openHours.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<String, Interval>>() {
            public int compare( Map.Entry<String, Interval> x, Map.Entry<String, Interval> y )
            {
                return x.getValue().getStart().compareTo(y.getValue().getStart());
            }
        });
        return list;
    }

    public String openingTime() {
        List<Map.Entry<String, Interval>> list = orderedHours();
        for (int i = 0; i < list.size(); i++) {
            Interval openInterval = list.get(i).getValue();
            if (openInterval.isAfterNow()) {
                return openInterval.getStart().toString("h:mma");
            }
        }
        return "";
    }

    public boolean isOpen() {
        for (Interval openInterval : openHours.values()) {
            if (openInterval.containsNow()) {
                return true;
            }
        }
        return false;
    }

    public String openMeal() {
        for (Map.Entry<String, Interval> entry : openHours.entrySet()) {
            Interval openInterval = entry.getValue();
            if (openInterval.containsNow()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String nextMeal() {
        List<Map.Entry<String, Interval>> list = orderedHours();
        for (int i = 0; i < list.size(); i++) {
            Interval openInterval = list.get(i).getValue();
            if (openInterval.isAfterNow()) {
                return list.get(i).getKey();
            }
        }
        return "";
    }

    /**
    * Created by Adel on 12/18/14.
            * Class for a single menu, ie. Lunch, Dinner
    */
    public static class Menu implements Parcelable{
        @SerializedName("txtDayPartDescription") public String name;
        @SerializedName("tblStation") public List<DiningStation> stations = new ArrayList<>();

        protected Menu(Parcel in) {
            name = in.readString();
        }

        public static final Creator<Menu> CREATOR = new Creator<Menu>() {
            @Override
            public Menu createFromParcel(Parcel in) {
                return new Menu(in);
            }

            @Override
            public Menu[] newArray(int size) {
                return new Menu[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
        }
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for a station at a dining hall
     */
    public static class DiningStation {
        @SerializedName("txtStationDescription") public String name;
        @SerializedName("tblItem") public List<FoodItem> items = new ArrayList<>();
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for Food items in Dining menus
     */
    public static class FoodItem {
        @SerializedName("txtTitle") public String title;
        @SerializedName("txtDescription") String description;
    }
}
