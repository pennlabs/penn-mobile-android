package com.pennapps.labs.pennmobile;

import android.os.Parcel;
import android.os.Parcelable;

public class DayTraffic implements Parcelable {

    private int[] traffic;

    private DayTraffic(String[] traffic) {
        for (int i = 0; i < traffic.length; i++) {
            if (traffic[i].equals("High")) {
                this.traffic[i] = 3;
            } else if (traffic[i].equals("Medium")) {
                this.traffic[i] = 2;
            } else {
                this.traffic[i] = 1;
            }
        }
    }

    protected DayTraffic(Parcel in) {
        traffic = in.createIntArray();
    }

    public static final Creator<DayTraffic> CREATOR = new Creator<DayTraffic>() {
        @Override
        public DayTraffic createFromParcel(Parcel in) {
            return new DayTraffic(in);
        }

        @Override
        public DayTraffic[] newArray(int size) {
            return new DayTraffic[size];
        }
    };

    private int[] getTraffic() {
        return traffic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(traffic);
    }
}