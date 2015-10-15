package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.pennapps.labs.pennmobile.classes.Review;

import java.util.List;

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
    @Bind(R.id.pcr_layout) LinearLayout pcrLayout;
    @Bind(R.id.course_avg_course) TextView courseQuality;
    @Bind(R.id.course_avg_instr) TextView instructorQuality;
    @Bind(R.id.course_avg_diff) TextView courseDifficulty;

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
        String buildingCode = course.getBuildingCode();
        final String meetingLocation = course.getMeetingLocation();
        if (!buildingCode.equals("")) {
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
        String days = course.getMeetingDays();
        String times = course.getMeetingStartTime();
        String markerText = days + " " + times + " " + meetingLocation;
        if (map != null && courseLatLng != null && mapFrame != null) {
            mapFrame.setVisibility(View.VISIBLE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(courseLatLng, 17));
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(courseLatLng)
                    .title(markerText));
            marker.showInfoWindow();
        }
    }

    private void findCourseReviews() {
        mLabs.course_review(course.course_department + "-" + course.course_number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Review>() {
                    @Override
                    public void call(Review review) {
                        pcrLayout.setVisibility(View.VISIBLE);
                        courseQuality.setText(review.courseQuality());
                        instructorQuality.setText(review.instructorQuality());
                        courseDifficulty.setText(review.difficulty());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void processCourse() {
        String activityText;
        String courseTitleText;
        String instructorsText;
        String courseDescription;

        drawCourseMap();

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

        findCourseReviews();
    }
}