package com.pennapps.labs.pennmobile;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.classes.Course;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrarFragment extends Fragment {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Course course;

    @Bind(R.id.course_code) TextView courseCodeTextView;
    @Bind(R.id.course_activity) TextView courseActivityTextView;
    @Bind(R.id.course_title) TextView courseTitleTextView;
    @Bind(R.id.instructor) TextView instructorTextView;
    @Bind(R.id.course_desc_title) TextView descriptionTitle;
    @Bind(R.id.course_desc) TextView descriptionTextView;
    @Bind(R.id.registrar_map_frame) View mapFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable("RegistrarFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registrar, container, false);
        ButterKnife.bind(this, v);
        processCourse();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().add(R.id.registrar_map_container, mapFragment).commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registrar);
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.95198, -75.19368), 17));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        }
    }

    public LatLng getBuildingLatLng(Course course) {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            if (course.meetings.size() > 0) {
                List<Address> locationList = geocoder.getFromLocationName(course.meetings.get(0).building_name, 1);
                if (locationList.size() > 0) {
                    return new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processCourse() {
        LatLng courseLatLng;
        Spannable courseCodeText;
        String activityText;
        String courseTitleText;
        String instructorsText;
        String courseDescription;
        String locationText = "";

        courseLatLng = getBuildingLatLng(course);
        courseCodeText = new SpannableString(
                course.course_department + " " +
                        String.format("%03d", course.course_number) + " " +
                        String.format("%03d", course.section_number));
        courseCodeText.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.color_primary_light)),
                courseCodeText.length() - 3,
                courseCodeText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        activityText = course.activity;
        if (course.meetings.size() > 0) {
            locationText = course.meetings.get(0).building_code + " " + course.meetings.get(0).room_number;
        }
        if (course.instructors.size() > 0) {
            instructorsText = course.instructors.get(0).name;
        } else {
            instructorsText = getString(R.string.professor_missing);
        }
        courseTitleText = course.course_title;
        courseDescription = course.course_description;

        try {
            courseCodeTextView.setText(courseCodeText);

            if (map != null && courseLatLng != null) {
                mapFrame.setVisibility(View.VISIBLE);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(courseLatLng, 17));
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(courseLatLng)
                        .title(locationText));
                marker.showInfoWindow();
            }
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
