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
    public String first_meeting_days;
    public List<Instructor> instructors = new ArrayList<>();
    public List<Meeting> meetings = new ArrayList<>();

    protected Course(Parcel in) {
        course_department = in.readString();
        course_number = in.readInt();
        section_number = in.readInt();
        course_title = in.readString();
        course_description = in.readString();
        activity = in.readString();
        first_meeting_days = in.readString();
        in.readList(instructors, Instructor.class.getClassLoader());
        in.readList(meetings, Meeting.class.getClassLoader());
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

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
        dest.writeString(first_meeting_days);
        dest.writeList(instructors);
        dest.writeList(meetings);
    }
}
