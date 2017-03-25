package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LaundryHall implements Parcelable{
    private String name;
    private ArrayList<LaundryRoom> laundries;

    public LaundryHall(String name, LaundryRoom laundryRoom) {
        this.name = name;
        laundries = new ArrayList<>();
        laundries.add(laundryRoom);
    }

    protected LaundryHall(Parcel in) {
        name = in.readString();
        laundries = in.createTypedArrayList(LaundryRoom.CREATOR);
    }

    public static final Creator<LaundryHall> CREATOR = new Creator<LaundryHall>() {
        @Override
        public LaundryHall createFromParcel(Parcel in) {
            return new LaundryHall(in);
        }

        @Override
        public LaundryHall[] newArray(int size) {
            return new LaundryHall[size];
        }
    };

    public static List<LaundryHall> getLaundryHall(List<LaundryRoom> laundries) {
        LinkedList<LaundryHall> halls = new LinkedList<>();
        for (LaundryRoom l : laundries) {
            LaundryHall h = null;
            if (!halls.isEmpty()) {
                h = halls.getLast();
            }
            String name = l.name;
            if (l.name.contains("-")) {
                name = l.name.substring(0, l.name.indexOf('-'));
            }
            if (h != null && h.getName().equals(name)) {
                h.getIds().add(l);
            } else {
                h = new LaundryHall(name,  l);
                halls.add(h);
            }
        }
        return halls;
    }

    public List<LaundryRoom> getIds() {
        return laundries;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(laundries);
    }
}
