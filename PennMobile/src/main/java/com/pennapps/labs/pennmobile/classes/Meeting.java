package com.pennapps.labs.pennmobile.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adel on 12/15/14.
 * Class for course meetings in course timetable
 */
public class Meeting implements Parcelable {
    public String building_code;
    public String building_name;
    public String room_number;
    public String section_id;
    public String meeting_days;
    public String start_time;
    public String end_time;

    protected Meeting(Parcel in) {
        building_code = in.readString();
        building_name = in.readString();
        room_number = in.readString();
        section_id = in.readString();
        meeting_days = in.readString();
        start_time = in.readString();
        end_time = in.readString();
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(building_code);
        parcel.writeString(building_name);
        parcel.writeString(room_number);
        parcel.writeString(section_id);
        parcel.writeString(meeting_days);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
    }
}
