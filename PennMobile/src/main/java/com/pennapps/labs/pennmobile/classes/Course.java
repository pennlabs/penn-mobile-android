package com.pennapps.labs.pennmobile.classes;


import java.util.ArrayList;
import java.util.List;

public class Course {
    public String course_department;
    public int course_number;
    public String course_title;
    public String course_description;
    public List<Instructor> instructors = new ArrayList<>();
}
