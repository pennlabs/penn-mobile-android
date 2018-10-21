package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
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
    private int image;
    @SerializedName("tblDayPart") public List<Menu> menus = new ArrayList<>();

    public DiningHall(int id, String name, boolean residential, HashMap<String, Interval> hours, Venue venue, int image) {
        this.id = id;
        this.name = name;
        this.residential = residential;
        this.openHours = hours;
        this.venue = venue;
        this.image = image;
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
        image = in.readInt();
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
        dest.writeInt(image);
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

    public List<String> getExtras(){
        return venue.getExtras();
    }

    public Venue getVenue(){
        return venue;
    }

    public int getImage() {
        return image;
    }

    // Returns list of time intervals sorted by interval starting time
    private List<Interval> orderedHours() {
        List<Interval> list = new ArrayList<>(openHours.values());
        Collections.sort( list, new Comparator<Interval>() {
            public int compare( Interval x, Interval y )
            {
                return x.getStart().compareTo(y.getStart());
            }
        });
        return list;
    }

    // Returns list of time intervals sorted by interval starting time, and merges intervals such that none overlap
    private List<Interval> orderedMergedHours() {
        List<Interval> originalList = orderedHours();
        List<Interval> mergedList = new ArrayList<>(originalList.size());
        Interval currentInterval = null;
        for (int i = 0; i < originalList.size(); i++) {
            if (currentInterval == null) {
                currentInterval = originalList.get(i);
            } else if (currentInterval.getEnd().compareTo(originalList.get(i).getStart()) >= 0) {
                DateTime newEndTime = currentInterval.getEnd().compareTo(originalList.get(i).getEnd()) > 0
                        ? currentInterval.getEnd() : originalList.get(i).getEnd();
                currentInterval = new Interval(currentInterval.getStart(), newEndTime);
            } else {
                mergedList.add(currentInterval);
                currentInterval = null;
            }
        }
        if (currentInterval != null) {
            mergedList.add(currentInterval);
        }
        return mergedList;
    }

    public String openTimes() {

        List<Interval> list = isResidential() ? orderedHours() : orderedMergedHours();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Interval openInterval = list.get(i);
            if (i != 0) {
                builder.append(" | ");
            }
            builder.append(getFormattedTime(openInterval.getStart()));
            builder.append(" - ");
            builder.append(getFormattedTime(openInterval.getEnd()));
        }
        return builder.toString();
    }

    private String getFormattedTime(DateTime time) {
        if (time.toString("mm").equals("00")) {
            if (isResidential()) {
                return time.toString("h");
            } else {
                return time.toString("h a");
            }
        } else {
            if (isResidential()) {
                return time.toString("h:mm");
            } else {
                return time.toString("h:mm a");
            }
        }
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
