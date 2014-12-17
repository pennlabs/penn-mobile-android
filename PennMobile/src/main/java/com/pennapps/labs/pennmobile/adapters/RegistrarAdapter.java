package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.List;

public class RegistrarAdapter extends ArrayAdapter<Course> {

    public RegistrarAdapter(Context context, int layout, List<Course> courses) {
        super(context, layout, courses);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.search_entry, null);

        TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
        TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
        TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);

        courseId.setText(course.course_department + course.course_number);
        try {
            courseInstr.setText(course.instructors.get(0).name);
        } catch (IndexOutOfBoundsException e) {
            courseInstr.setText("");
        }
        courseTitle.setText(course.course_title);
        view.setTag(courseId.getText());
        return view;
    }
}