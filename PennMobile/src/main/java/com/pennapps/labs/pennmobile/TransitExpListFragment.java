package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TransitExpListFragment extends Fragment {

    private TransitAPI mAPI;
    private ArrayList<BusStop> mTransitArr;
    private ExpandableListView mExpLV;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transit_exp, null);
        mExpLV = (ExpandableListView) v.findViewById(R.id.listView);
        // mExpLV.setAdapter(new NewExpListViewAdapter());
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        LocationManager service = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            // TODO: sigh
        }

        mAPI = new TransitAPI();
        mAPI.setUrlPath("transit/stopinventory");

        Log.v("vivlabs", service.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString());
        Location mLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // 39.952960, -75.201339
        // new GetRequestTask(mLocation.getLatitude(), mLocation.getLongitude()).execute();
        new GetStopsTask(39.952960, -75.201339).execute();
    }

    private class GetStopsTask extends AsyncTask<Void, Void, Boolean> {

        private JSONArray responseArr;
        private double latitude;
        private double longitude;

        GetStopsTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getCourse("");
                responseArr = (JSONArray) resultObj.get("result_data");
                if (responseArr.length() == 0) return false;
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                Log.v("vivlabs", "Invalid?");
                return;
            }

            try {
                mTransitArr = new ArrayList<BusStop>();

                for (int i = 0; i < responseArr.length(); i++) {
                    JSONObject resp = (JSONObject) responseArr.get(i);
                    if (resp.has("BusStopName")) {
                        Log.v("vivlabs", resp.toString());
                        BusStop stop = new BusStop(resp.get("BusStopId").toString(),
                                                   resp.get("BusStopName").toString(),
                                                   resp.get("Latitude").toString(),
                                                   resp.get("Longitude").toString());
                        mTransitArr.add(stop);
                    }
                }

                ArrayList<BusStop> distanceArr = new ArrayList<BusStop>();
                for (int i = 0; i < mTransitArr.size(); i++) {
                    BusStop currentStop = mTransitArr.get(i);
                    double x = Math.abs(currentStop.getLatitude() - latitude);
                    double y = Math.abs(currentStop.getLongitude() - longitude);
                    double distance = Math.sqrt(x * x + y * y);
                    distanceArr.add(new BusStop(currentStop.getName(), distance));
                }

                Collections.sort(distanceArr);
                mExpLV.setAdapter(new NewExpListViewAdapter(distanceArr, getActivity()));

                /*
                for (int i = 0; i < distanceArr.size(); i++) {
                    Log.v("vivlabs", distanceArr.get(i).getName() + " " + distanceArr.get(i).getDistance());
                }
                */


            } catch (JSONException e) {
                Log.v("vivlabs", "" + e);
            }
        }
    }

    private class GetRoutesTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray responseArr;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getCourse("transit/511/Configuration");
                responseArr = (JSONArray) resultObj.get("result_data");
                if (responseArr.length() == 0) return false;
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {

        }
    }

    public class NewExpListViewAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;
        private Activity mActivity;
        private ArrayList<BusStop> mStopsList;

        /*
        private Course[] instructors = {
                new Course("CIS 110", "Intro to Comp Prog", "Benedict Brown", "2.69", "2.40", "3.31"),
                new Course("CIS 110", "Intro to Comp Prog", "Peter-Michael Osera", "2.95", "3.09", "2.90")
        };
        */

        private String[] instructors = {"Benedict Brown", "Peter-Michael Osera"};

        private String[][] children = {
                {"1", "2", "3"},
                {"4", "5", "6"}
        };

        NewExpListViewAdapter(ArrayList<BusStop> stops, Activity activity) {
            mStopsList = stops;
            mActivity = activity;
        }

        public void setInflater(LayoutInflater inflater, Activity act) {
            this.mInflater = inflater;
            mActivity = act;
        }

        @Override
        public int getGroupCount() {
            return mStopsList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 2;
            // return children[groupPosition].length;
            // return mStopsList.get(groupPosition).size();
        }

        @Override
        public BusStop getGroup(int groupPosition) {
            // return instructors[groupPosition];
            return mStopsList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // return children[groupPosition][childPosition];
            return children[0][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mActivity);
            textView.setText(getGroup(groupPosition).getName());
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mActivity);
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
