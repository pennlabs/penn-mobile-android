package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 10/21/2015.
 */
public class LaundryRoom implements Parcelable {
    public int dryers_available;
    public int dryers_in_use;
    public int hall_no;
    public String name;
    public int washers_available;
    public int washers_in_use;

    protected LaundryRoom(Parcel in) {
        dryers_available = in.readInt();
        dryers_in_use = in.readInt();
        hall_no = in.readInt();
        name = in.readString();
        washers_available = in.readInt();
        washers_in_use = in.readInt();
    }

    public static final Creator<LaundryRoom> CREATOR = new Creator<LaundryRoom>() {
        @Override
        public LaundryRoom createFromParcel(Parcel in) {
            return new LaundryRoom(in);
        }

        @Override
        public LaundryRoom[] newArray(int size) {
            return new LaundryRoom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dryers_available);
        dest.writeInt(dryers_in_use);
        dest.writeInt(hall_no);
        dest.writeString(name);
        dest.writeInt(washers_available);
        dest.writeInt(washers_in_use);
    }
}
