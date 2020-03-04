package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrarAdapter extends ArrayAdapter<Course> {
    private final LayoutInflater inflater;
    private List<Course> courses;
    private Context mContext;

    public RegistrarAdapter(Context context, List<Course> courses) {
        super(context, R.layout.registrar_list_item, courses);
        this.courses = courses;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Course course = getItem(position);
        String courseName = course.getName();
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.registrar_list_item, parent, false);
            holder = new ViewHolder(view, course);
            view.setTag(holder);
        }

        holder.course = course;

        Spannable courseCode = new SpannableString(courseName);
        courseCode.setSpan(
                new ForegroundColorSpan(view.getResources().getColor(R.color.secondary_text_default_material_light)),
                courseCode.length() - 3,
                courseCode.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.courseId.setText(courseCode);
        try {
            holder.courseInstr.setEllipsize(TextUtils.TruncateAt.END);
            holder.courseInstr.setMaxLines(1);
            holder.courseInstr.setText(course.instructors.get(0).name);
            holder.courseInstr.setTextColor(Color.BLACK);
        } catch (IndexOutOfBoundsException e) {
            holder.courseInstr.setText(getContext().getString(R.string.professor_missing));
            holder.courseInstr.setTextColor(Color.parseColor("#4a000000"));
        }

        StringBuilder meetTimes = new StringBuilder(course.getMeetingDays());
        meetTimes.append(" ");
        meetTimes.append(course.getMeetingStartTime());
        meetTimes.append(" - ");
        meetTimes.append(course.getMeetingEndTime());

        holder.courseTitle.setEllipsize(TextUtils.TruncateAt.END);
        holder.courseTitle.setMaxLines(1);
        holder.courseTitle.setText(course.course_title);
        holder.courseActivity.setText(course.activity);
        holder.courseTimes.setText(meetTimes.toString());
        holder.courseTimes.setTextColor(view.getResources().getColor(R.color.settings_grey));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        Set<String> starredCourses = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_reg_star), new HashSet<String>());
        holder.star.setChecked(starredCourses.contains(course.getId()));
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                Set<String> buffer = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_reg_star), new HashSet<String>());
                Set<String> starredCourses = new HashSet<>(buffer);
                SharedPreferences.Editor editedPreferences = sharedPref.edit();
                ToggleButton star = (ToggleButton) v;
                boolean starred = star.isChecked();
                String currentCourse = course.getId();
                if (starred) {
                    if (currentCourse != null) {
                        starredCourses.add(currentCourse);
                        editedPreferences.putString(currentCourse + mContext.getResources().getString(R.string.search_reg_star),
                                getDataString(course));
                    }
                } else {
                    starredCourses.remove(currentCourse);
                    if (currentCourse != null) {
                        editedPreferences.remove(currentCourse + mContext.getResources().getString(R.string.search_reg_star));
                    }
                }
                editedPreferences.putStringSet(mContext.getResources().getString(R.string.search_reg_star), starredCourses);
                editedPreferences.apply();
            }
        });

        return view;
    }

    public static class ViewHolder {
        @BindView(R.id.course_id_text) TextView courseId;
        @BindView(R.id.course_instr_text) TextView courseInstr;
        @BindView(R.id.course_title_text) TextView courseTitle;
        @BindView(R.id.course_meeting_times) TextView courseTimes;
        @BindView(R.id.star_course) ToggleButton star;
        @BindView(R.id.course_activity) TextView courseActivity;
        public Course course;

        public ViewHolder(View view, Course course) {
            this.course = course;
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return courses != null ? courses.size() : 0;
    }

    private String getDataString(Course currentCourse){
        return (new Gson()).toJson(currentCourse, Course.class);
    }
}