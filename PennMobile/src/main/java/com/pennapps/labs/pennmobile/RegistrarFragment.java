package com.pennapps.labs.pennmobile;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.RegistrarAPI;
import com.pennapps.labs.pennmobile.pcr.RegCourse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class RegistrarFragment extends Fragment {

    private RegistrarAPI mAPI;
    private TextView mTextView;
    private TextView courseCodeTextView;
    private TextView courseTitleTextView;
    private TextView instructorTextView;
    private TextView locationTextView;
    private TextView descriptionTitle;
    private TextView descriptionTextView;
    private View mapFrame;
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = new RegistrarAPI();
        new GetRequestTask(getArguments().getString(RegistrarSearchFragment.COURSE_ID_EXTRA)).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registrar, container, false);
        mTextView = (TextView) v.findViewById(R.id.temp);
        courseCodeTextView = (TextView) v.findViewById(R.id.course_code);
        courseTitleTextView = (TextView) v.findViewById(R.id.course_title);
        instructorTextView = (TextView) v.findViewById(R.id.instructor);
        locationTextView = (TextView) v.findViewById(R.id.location);
        descriptionTitle = (TextView) v.findViewById(R.id.course_desc_title);
        descriptionTextView = (TextView) v.findViewById(R.id.course_desc);
        mapFrame = v.findViewById(R.id.registrar_map_frame);
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
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                map.addMarker(new MarkerOptions().position(new LatLng(39.95198, -75.19368)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.95198, -75.19368), 17));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        }
    }
    public LatLng getBuildingLatLng(RegCourse course) {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            List<Address> locationList = geocoder.getFromLocationName(course.getBuildingName(), 1);
            try {
                return new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private String input;
        private JSONObject resp;

        GetRequestTask(String s) {
            input = s;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                JSONObject resultObj = mAPI.getCourse(input);
                JSONArray responseArr = (JSONArray) resultObj.get("courses");
                if (responseArr.length() == 0) {
                    return false;
                }
                resp = (JSONObject) responseArr.get(0);
                return true;
            } catch(JSONException e) {
                return false;
            } catch(Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                // sort of sloppy :/
                mTextView.setText(input + " is not currently offered.");
                return;
            }
            try {
                JSONObject meetings = (JSONObject) ((JSONArray) resp.get("meetings")).get(0);
                JSONArray instrJSON = (JSONArray) resp.get("instructors");
                String[] instrArr = new String[instrJSON.length()];
                for (int i = 0; i < instrJSON.length(); i++) {
                    instrArr[i] = ((JSONObject) instrJSON.get(i)).get("name").toString();
                }

                RegCourse course = new RegCourse.Builder(resp.get("activity").toString(),
                                        resp.get("course_department").toString(),
                                        resp.get("course_number").toString()).
                                        course_description(resp.get("course_description").toString()).
                                        course_title(resp.get("course_title").toString()).
                                        instructors(instrArr).
                                        building_code(meetings.get("building_code").toString()).
                                        building_name(meetings.get("building_name").toString()).
                                        room_number(meetings.get("room_number").toString()).
                                        start_time(meetings.get("start_time").toString()).
                                        end_time(meetings.get("end_time").toString()).
                                        section_id(meetings.get("section_id_normalized").toString()).
                                        build();

                LatLng courseLatLng = getBuildingLatLng(course);

                if (map != null) {
                    if (courseLatLng != null) {
                        mapFrame.setVisibility(View.VISIBLE);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(courseLatLng, 17));
                    }
                }

                String courseCodeText = course.getCourseDept() + " " + course.getCourseNumber();
                courseCodeTextView.setText(courseCodeText);

                String courseTitleText = course.getCourseTitle();
                courseTitleTextView.setText(courseTitleText);

                String instructorsText = course.getInstructors()[0];
                instructorTextView.setText(instructorsText);

                if (course.getBuildingName().equals("")) {
                    locationTextView.setVisibility(View.GONE);
                } else {
                    String locationText = course.getBuildingCode() + " " + course.getRoomNumber();
                    locationTextView.setText(locationText);
                }

                String courseDescription = course.getCourseDesc();
                if (courseDescription.equals("")) {
                    descriptionTitle.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.GONE);
                } else {
                    descriptionTitle.setVisibility(View.VISIBLE);
                    descriptionTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setText(courseDescription);
                }
            } catch (JSONException ignored) {

            } catch (NullPointerException ignored) {

            }
        }
    }

}
