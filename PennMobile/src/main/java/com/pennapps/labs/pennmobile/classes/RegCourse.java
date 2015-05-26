package com.pennapps.labs.pennmobile.classes;


public class RegCourse {

    private String activity;               // LEC
    private String course_description;     // <text>
    private String course_department;      // CIS
    private String course_number;          // 110
    private String course_title;           // Intro to...
    private String building_code;          // TOWN
    private String building_name;          // Towne Building
    private String room_number;            // 100
    private String start_time;             // 10:00 AM
    private String end_time;               // 11:00 AM
    private String section_id;             // CIS -110-001
    private String[] instructors;          // [{name: }]

    public static class Builder {
        // required
        private String activity;               // LEC
        private String course_department;
        private String course_number;

        // optional
        private String course_description = "";     // <text>
        private String course_title = "";           // Intro to...
        private String building_code = "";          // TOWN
        private String building_name = "";          // Towne Building
        private String room_number = "";            // 100
        private String start_time = "";             // 10:00 AM
        private String end_time = "";               // 11:00 AM
        private String section_id = "";             // 'normalized' CIS -110-001
        private String[] instructors = {};          // [{name: }]

        public Builder(String activity, String course_department, String course_number) {
            this.activity = activity;
            this.course_department = course_department;
            this.course_number = course_number;
        }

        public Builder course_description(String input) {
            course_description = input;
            return this;
        }

        public Builder course_title(String input) {
            course_title = input;
            return this;
        }

        public Builder building_code(String input) {
            building_code = input;
            return this;
        }

        public Builder building_name(String input) {
            building_name = input;
            return this;
        }

        public Builder room_number(String input) {
            room_number = input;
            return this;
        }

        public Builder start_time(String input) {
            start_time = input;
            return this;
        }

        public Builder end_time(String input) {
            end_time = input;
            return this;
        }

        public Builder section_id(String input) {
            section_id = input;
            return this;
        }

        public Builder instructors(String[] input) {
            instructors = input;
            return this;
        }

        public RegCourse build() {
            return new RegCourse(this);
        }
    }

    private RegCourse(Builder builder) {
        activity           = builder.activity;
        course_department  = builder.course_department;
        course_number      = builder.course_number;
        course_description = builder.course_description;
        course_title       = builder.course_title;
        building_code      = builder.building_code;
        building_name      = builder.building_name;
        room_number        = builder.room_number;
        start_time         = builder.start_time;
        end_time           = builder.end_time;
        section_id         = builder.section_id;
        instructors        = builder.instructors;
    }

    public String getActivity() {
        return activity;
    }

    public String getCourseDept() {
        return course_department;
    }

    public String getCourseNumber() {
        return course_number;
    }

    public String getCourseDesc() {
        return course_description;
    }

    public String getCourseTitle() {
        return course_title;
    }

    public String getBuildingCode() {
        return building_code;
    }

    public String getBuildingName() {
        return building_name;
    }

    public String getRoomNumber() {
        return room_number;
    }

    public String getStartTime() {
        return start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public String getSectionId() {
        return section_id;
    }

    public String[] getInstructors() {
        return instructors;
    }
}
