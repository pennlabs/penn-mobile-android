package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomSimple implements Parcelable {

    public String id;
    public String name;

    protected LaundryRoomSimple(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<LaundryRoomSimple> CREATOR = new Creator<LaundryRoomSimple>() {
        @Override
        public LaundryRoomSimple createFromParcel(Parcel in) {
            return new LaundryRoomSimple(in);
        }

        @Override
        public LaundryRoomSimple[] newArray(int size) {
            return new LaundryRoomSimple[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}
