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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.MapCallbacks;
import com.pennapps.labs.pennmobile.classes.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CourseFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.course_activity) TextView courseActivityTextView;
    @BindView(R.id.course_title) TextView courseTitleTextView;
    @BindView(R.id.instructor) TextView instructorTextView;
    @BindView(R.id.course_desc_title) TextView descriptionTitle;
    @BindView(R.id.course_desc) TextView descriptionTextView;
    @BindView(R.id.registrar_map_frame) View mapFrame;
    @BindView(R.id.pcr_layout) LinearLayout pcrLayout;
    @BindView(R.id.course_avg_course) TextView courseQuality;
    @BindView(R.id.course_avg_instr) TextView instructorQuality;
    @BindView(R.id.course_avg_diff) TextView courseDifficulty;
    private Unbinder unbinder;

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Course course;
    private Labs mLabs;
    private MainActivity mActivity;
    private boolean fav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable(getString(R.string.course_bundle_arg));
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        fav = getArguments().getBoolean(getString(R.string.search_favorite), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_course, container, false);
        unbinder = ButterKnife.bind(this, v);
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
        if (menu != null) {
            MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);
            if (searchMenuItem != null) {
                SearchView searchView = (SearchView) searchMenuItem.getActionView();
                searchView.setEnabled(false);
                searchMenuItem.setVisible(false);
                searchView.clearFocus();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int pos = SearchFavoriteFragment.getPagePosition();
                if (RegistrarTab.fragments[pos] == null) {
                    pos = (pos + 1) % 2;
                }
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(RegistrarTab.fragments[pos]).commit();
                RegistrarTab.fragments[pos] = null;
//                if (RegistrarTab.fragments[0] == null && RegistrarTab.fragments[1] == null) {
//                    mActivity.getActionBarToggle().setDrawerIndicatorEnabled(true);
//                    mActivity.getActionBarToggle().syncState();
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (containsNum(getActivity().getTitle())) {
            StringBuilder builder = new StringBuilder(getActivity().getTitle());
            boolean fav = getString(R.string.registrar_search) != null && getArguments().getBoolean(getString(R.string.registrar_search), false);
            if (fav) {
                builder.append(" - ").append(course.getName());
            } else {
                builder.insert(0, " - ").insert(0, course.getName());
            }
            getActivity().setTitle(builder.toString());
        } else {
            getActivity().setTitle(course.getName());
        }
        if (map == null) {
            mapFragment.getMapAsync(this);
        }
        processCourse();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapCallbacks.DEFAULT_LATLNG, 17));
            map.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity().getTitle().toString().contains("-")) {
            StringBuilder builder = new StringBuilder(getActivity().getTitle());
            boolean fav = getString(R.string.registrar_search) != null && getArguments().getBoolean(getString(R.string.registrar_search), false);
            if (fav) {
                builder.delete(builder.indexOf(" - "), builder.length());
            } else {
                builder.delete(0, builder.indexOf(" - ") + 2);
            }
            getActivity().setTitle(builder.toString());
        } else {
            getActivity().setTitle(R.string.registrar);
        }
        unbinder.unbind();
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
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
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

    public static boolean containsNum(CharSequence cs){
        String s = cs.toString();
        for (char c: s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}