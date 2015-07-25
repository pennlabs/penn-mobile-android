package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adel on 12/13/14.
 * Class for instructors in the registrar
 */
public class Instructor implements Parcelable {
    public String name;

    protected Instructor(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Instructor> CREATOR = new Creator<Instructor>() {
        @Override
        public Instructor createFromParcel(Parcel in) {
            return new Instructor(in);
        }

        @Override
        public Instructor[] newArray(int size) {
            return new Instructor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }
}
