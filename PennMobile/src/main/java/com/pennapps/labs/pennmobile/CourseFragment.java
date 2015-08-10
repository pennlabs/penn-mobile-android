package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CourseFragment extends Fragment {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Course course;
    private Labs mLabs;

    @Bind(R.id.course_activity) TextView courseActivityTextView;
    @Bind(R.id.course_title) TextView courseTitleTextView;
    @Bind(R.id.instructor) TextView instructorTextView;
    @Bind(R.id.course_desc_title) TextView descriptionTitle;
    @Bind(R.id.course_desc) TextView descriptionTextView;
    @Bind(R.id.registrar_map_frame) View mapFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable("CourseFragment");
        mLabs = MainActivity.getLabsInstance();
        ((MainActivity) getActivity()).closeKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_course, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        FragmentManager fm = getChildFragmentManager();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().add(R.id.registrar_map_container, mapFragment).commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setEnabled(false);
        searchMenuItem.setVisible(false);
        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(course.getName());
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.95198, -75.19368), 17));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        }
        processCourse();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.registrar);
        RegistrarFragment.hideSearchView = true;
        ButterKnife.unbind(this);
    }

    private void drawCourseMap() {
        String buildingCode = "";
        final String meetingLocation;
        // Check if course has meetings and building code is not empty
        if (!course.meetings.isEmpty() && !course.meetings.get(0).building_code.equals("")) {
            buildingCode = course.meetings.get(0).building_code;
            meetingLocation = course.meetings.get(0).building_code + course.meetings.get(0).room_number;
        } else if (!course.first_meeting_days.equals("")) {
            // Fallback for empty building code, useful before semester starts
            // Regex gets building code after AM/PM
            // Ex: "MWF12:00 PMTOWN100" -> "TOWN"
            buildingCode = getRegex(course.first_meeting_days, "(?<=\\s(A|P)M)[A-Z]+");
            meetingLocation = getRegex(course.first_meeting_days, "(?<=\\s(A|P)M)\\w+");
        } else {
            meetingLocation = "";
        }
        if (buildingCode != null && !buildingCode.equals("")) {
            mLabs.buildings(buildingCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Building>>() {
                        @Override
                        public void call(List<Building> buildings) {
                            if (!buildings.isEmpty()) {
                                drawMarker(buildings.get(0).getLatLng(), meetingLocation);
                            }
                        }
                    });
        }
    }

    private void drawMarker(LatLng courseLatLng, String meetingLocation) {
        String days = "";
        String times = "";
        if (!course.meetings.isEmpty()) {
            days = course.meetings.get(0).meeting_days;
            times = course.meetings.get(0).start_time;
        } else if (!course.first_meeting_days.equals("")) {
            days = getRegex(course.first_meeting_days, "^[a-zA-Z]+");
            times = getRegex(course.first_meeting_days, "([\\d]{1,2}:[\\d]{1,2}\\s*[aApP][mM])");
        }
        String markerText = days + " " + times + " " + meetingLocation;
        if (map != null && courseLatLng != null) {
            mapFrame.setVisibility(View.VISIBLE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(courseLatLng, 17));
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(courseLatLng)
                    .title(markerText));
            marker.showInfoWindow();
        }
    }

    @NonNull
    private String getRegex(String string, String pattern) {
        Matcher m = Pattern.compile(pattern).matcher(string);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    private void processCourse() {
        Spannable courseCodeText;
        String activityText;
        String courseTitleText;
        String instructorsText;
        String courseDescription;

        drawCourseMap();

        courseCodeText = new SpannableString(course.getName());
        courseCodeText.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.color_primary_light)),
                courseCodeText.length() - 3,
                courseCodeText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        activityText = course.activity;
        if (course.instructors.size() > 0) {
            instructorsText = course.instructors.get(0).name;
        } else {
            instructorsText = getString(R.string.professor_missing);
        }
        courseTitleText = course.course_title;
        courseDescription = course.course_description;

        try {
            courseActivityTextView.setText(activityText);
            courseTitleTextView.setText(courseTitleText);
            instructorTextView.setText(instructorsText);
            if (instructorsText.equals(getString(R.string.professor_missing))) {
                instructorTextView.setTextColor(getResources().getColor(R.color.color_primary_light));
            }

            if (courseDescription.equals("")) {
                descriptionTitle.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTitle.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(courseDescription);
            }
        } catch (NullPointerException ignored) {

        }
    }
}