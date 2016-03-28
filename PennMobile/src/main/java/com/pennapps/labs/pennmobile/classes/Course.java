package com.pennapps.labs.pennmobile.classes;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @NonNull
    private String getRegex(String string, String pattern) {
        Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(string);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public String getName() {
        return course_department + " " +
                String.format("%03d", course_number) + " " +
                String.format("%03d", section_number);
    }

    @NonNull
    public String getBuildingCode() {
        if (!meetings.isEmpty() && !meetings.get(0).building_code.equals("")) {
            return meetings.get(0).building_code;
        } else if (!first_meeting_days.equals("")) {
            // Fallback for empty building code, useful before semester starts
            // Regex gets building code after AM/PM
            // Ex: "MWF12:00 PMTOWN100" -> "TOWN"
            return getRegex(first_meeting_days, "(?<=\\s(A|P)M)[A-Z]+");
        }
        return "";
    }

    @NonNull
    public String getMeetingLocation() {
        if (!meetings.isEmpty() && !meetings.get(0).building_code.equals("")) {
            return meetings.get(0).building_code + meetings.get(0).room_number;
        } else if (!first_meeting_days.equals("")) {
            // Fallback for empty building code, useful before semester starts
            // Regex gets building code and room number after AM/PM
            // Ex: "MWF12:00 PMTOWN100" -> "TOWN100"
            return getRegex(first_meeting_days, "(?<=\\s(A|P)M)\\w{3,4}");
        }
        return "";
    }

    @NonNull
    public String getMeetingDays() {
        if (!meetings.isEmpty()) {
            return meetings.get(0).meeting_days;
        } else if (!first_meeting_days.equals("")) {
            // Regex gets the days which are alphabetic at start of string
            // Ex: "MWF12:00 PMTOWN100" -> "MWF"
            return getRegex(first_meeting_days, "^[A-Z]+");
        }
        return "";
    }

    @NonNull
    public String getMeetingStartTime() {
        if (!meetings.isEmpty()) {
            return meetings.get(0).start_time;
        } else if (!first_meeting_days.equals("")) {
            // Regex gets time
            // Ex: "MWF12:00 PMTOWN100" -> "12:00 PM"
            return getRegex(first_meeting_days, "(\\d{1,2}:\\d{1,2}\\s*[AP]M)");
        }
        return "";
    }

    @NonNull
    public String getMeetingEndTime(){
        if (!meetings.isEmpty()) {
            return meetings.get(0).end_time;
        }
        return "";
    }

    public String getId() {
        return new StringBuilder().append(course_department).append(course_number).append(section_number).toString();
    }
}
