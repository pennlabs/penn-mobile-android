package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 10/21/2015.
 */
public class LaundryMachine implements Parcelable {
    public boolean available;
    public String machine_type;
    public int number;
    public String time_left;
    public static final int MINUTE_TO_MILL = 60000;

    protected LaundryMachine(Parcel in) {
        available = in.readByte() != 0;
        machine_type = in.readString();
        number = in.readInt();
        time_left = in.readString();
    }

    public static final Creator<LaundryMachine> CREATOR = new Creator<LaundryMachine>() {
        @Override
        public LaundryMachine createFromParcel(Parcel in) {
            return new LaundryMachine(in);
        }

        @Override
        public LaundryMachine[] newArray(int size) {
            return new LaundryMachine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeString(machine_type);
        dest.writeInt(number);
        dest.writeString(time_left);
    }

    public long getTimeInMilli(){
        long answer;
        int i = 0;
        while(Character.isDigit(time_left.charAt(i))){
            i++;
        }
        if (time_left.substring(0, i).isEmpty()) {
            return 0;
        }
        answer = Integer.parseInt(time_left.substring(0,i));
        answer *= MINUTE_TO_MILL;
        return answer;
    }
}
