package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomSimple implements Parcelable {

    @SerializedName("hall_name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("location")
    @Expose
    public String location;

    protected LaundryRoomSimple(Parcel in) {
        id = in.readInt();
        name = in.readString();
        location = in.readString();
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
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(location);
    }
}
