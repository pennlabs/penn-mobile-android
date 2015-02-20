package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.registrar_list_item, null);

        TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
        TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
        TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);
        TextView courseActivity = (TextView) view.findViewById(R.id.course_activity);

        Spannable courseCode = new SpannableString(
                course.course_department +
                String.format("%03d", course.course_number) + " " +
                String.format("%03d", course.section_number)
        );
        courseCode.setSpan(
                new ForegroundColorSpan(view.getResources().getColor(R.color.secondary_text_default_material_light)),
                courseCode.length() - 3,
                courseCode.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        courseCode.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        courseId.setText(courseCode);
        try {
            courseInstr.setText(course.instructors.get(0).name);
        } catch (IndexOutOfBoundsException e) {
            courseInstr.setText(getContext().getString(R.string.professor_missing));
            courseInstr.setTextColor(Color.parseColor("#4a000000"));
        }
        courseTitle.setText(course.course_title);
        courseActivity.setText(course.activity);
        try {
            view.setTag(course.meetings.get(0).section_id);
        } catch (IndexOutOfBoundsException e) {
            view.setTag(courseId.getText());
        }
        return view;
    }
}