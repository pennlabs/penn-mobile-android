package com.pennapps.labs.pennmobile;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CourseActivity extends AppCompatActivity {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Course course;
    private Labs mLabs;

    @Bind(R.id.course_code) TextView courseCodeTextView;
    @Bind(R.id.course_activity) TextView courseActivityTextView;
    @Bind(R.id.course_title) TextView courseTitleTextView;
    @Bind(R.id.instructor) TextView instructorTextView;
    @Bind(R.id.course_desc_title) TextView descriptionTitle;
    @Bind(R.id.course_desc) TextView descriptionTextView;
    @Bind(R.id.registrar_map_frame) View mapFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getIntent().getExtras().getParcelable("Course");
        mLabs = MainActivity.getLabsInstance();
        setContentView(R.layout.activity_course);
        ButterKnife.bind(this);
        FragmentManager fm = getSupportFragmentManager();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().add(R.id.registrar_map_container, mapFragment).commit();
            fm.executePendingTransactions();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public LatLng getBuildingLatLng(Course course, int courseCodeLength) {
        LatLng latLng = null;
        String pattern = "(?<=\\s(A|P)M)\\w{" + courseCodeLength + "}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(course.first_meeting_days);
        if (m.find()) {
            List<Building> results = mLabs.buildings(m.group(0)).toBlocking().single();
            if (!results.isEmpty()) {
                latLng = results.get(0).getLatLng();
            }
        }
        return latLng;
    }

    private void processCourse() {
        LatLng courseLatLng;
        Spannable courseCodeText;
        String activityText;
        String courseTitleText;
        String instructorsText;
        String courseDescription;
        String locationText;

        courseLatLng = getBuildingLatLng(course, 3);
        if (courseLatLng == null) {
            courseLatLng = getBuildingLatLng(course, 4);
        }
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
        locationText = course.first_meeting_days;
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

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.course);
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.95198, -75.19368), 17));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        }
        processCourse();
    }
}
