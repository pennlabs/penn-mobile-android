package com.pennapps.labs.pennmobile;


public class Course {

    private String courseCode;
    private String courseName;
    private String instrName;
    private String avgCourse;
    private String avgInstr;
    private String avgDiff;

    Course(String courseCode, String courseName, String instrName,
           String avgCourse, String avgInstr, String avgDiff) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instrName  = instrName;
        this.avgCourse = avgCourse;
        this.avgInstr = avgInstr;
        this.avgDiff = avgDiff;
    }
}
