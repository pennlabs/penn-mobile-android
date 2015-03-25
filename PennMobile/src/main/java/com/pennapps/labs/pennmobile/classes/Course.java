package com.pennapps.labs.pennmobile.classes;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Course implements Parcelable {
    public String course_department;
    public int course_number;
    public int section_number;
    public String course_title;
    public String course_description;
    public String activity;
    public List<Instructor> instructors = new ArrayList<>();
    public List<Meeting> meetings = new ArrayList<>();

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(course_department);
        dest.writeInt(course_number);
        dest.writeInt(section_number);
        dest.writeString(course_title);
        dest.writeString(course_description);
        dest.writeString(activity);
        dest.writeList(instructors);
        dest.writeList(meetings);
    }
}
