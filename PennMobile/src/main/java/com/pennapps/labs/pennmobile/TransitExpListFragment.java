package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.pennapps.labs.pennmobile.adapters.NewExpListViewAdapter;
import com.pennapps.labs.pennmobile.api.TransitAPI;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusRouteStop;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.BusStopDist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TransitExpListFragment extends Fragment {

    private TransitAPI mAPI;
    private ArrayList<BusStop> mTransitArr;
    private ExpandableListView mExpLV;
    private Activity mActivity;
    private ArrayList<BusRoute> mRoutes;
    private NewExpListViewAdapter mAdapter;
    private ArrayList<BusStopDist> mDistanceArr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transit_exp, null);
        mExpLV = (ExpandableListView) v.findViewById(R.id.listView);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mRoutes = new ArrayList<BusRoute>();
        mDistanceArr = new ArrayList<BusStopDist>();
        LocationManager service = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            // TODO: sigh
        }

        mAPI = new TransitAPI();
        // mAPI.setUrlPath("stopinventory");

        Location mLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // new GetRequestTask(mLocation.getLatitude(), mLocation.getLongitude()).execute();
        new GetStopsTask(39.952960, -75.201339).execute();
        new GetRoutesTask().execute();
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
                JSONObject resultObj = mAPI.getStops();
                responseArr = (JSONArray) resultObj.get("result_data");
                return responseArr.length() != 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                return;
            }

            try {
                mTransitArr = new ArrayList<BusStop>();

                for (int i = 0; i < responseArr.length(); i++) {
                    JSONObject resp = (JSONObject) responseArr.get(i);
                    if (resp.has("BusStopName")) {
                        BusStop stop = new BusStop(resp.get("BusStopId").toString(),
                                                   resp.get("BusStopName").toString(),
                                                   resp.get("Latitude").toString(),
                                                   resp.get("Longitude").toString());
                        mTransitArr.add(stop);
                    }
                }

                for (BusStop currentStop : mTransitArr) {
                    double x = Math.abs(currentStop.getLatitude() - latitude);
                    double y = Math.abs(currentStop.getLongitude() - longitude);
                    double distance = Math.sqrt(x * x + y * y);
                    mDistanceArr.add(new BusStopDist(currentStop.getName(), distance));
                }

                Collections.sort(mDistanceArr);

                /*
                for (int i = 0; i < distanceArr.size(); i++) {
                    Log.v("vivlabs", distanceArr.get(i).getName() + " " + distanceArr.get(i).getDistance());
                }
                */


            } catch (JSONException ignored) {

            }
        }
    }

    private class GetRoutesTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray responseArr;
        // go through routes, sort by stop name --> route name
        HashMap<String, ArrayList<BusRoute>> routesByStop = new HashMap<String, ArrayList<BusRoute>>();

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getStop(511);
                responseArr = (JSONArray) ((JSONObject) ((JSONObject) resultObj.get("result_data")).get("ConfigurationData")).get("Route");
                return responseArr.length() != 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            try {
                int count = 0;
                for (int i = 0; i < responseArr.length(); i++) {
                    JSONObject responseObj = (JSONObject) responseArr.get(i);

                    String routeDescription = responseObj.get("title").toString();
                    String routeTitle       = responseObj.get("key").toString();
                    ArrayList<BusRouteStop> routeStops = new ArrayList<BusRouteStop>();

                    JSONArray stopsOnRoute = (JSONArray) (((JSONObject) ((JSONArray) responseObj.get("Direction")).get(0)).get("Stop"));

                    for (int j = 0; j < stopsOnRoute.length(); j++) {
                        JSONObject currentStop = (JSONObject) stopsOnRoute.get(j);
                        String stopTitle = currentStop.get("title").toString();
                        routeStops.add(new BusRouteStop(currentStop.get("key").toString(),
                                                        currentStop.get("stopOrder").toString(),
                                                        stopTitle));
                        ArrayList<BusRoute> tempList;
                        if (!routesByStop.containsKey(stopTitle)) {
                            tempList = new ArrayList<BusRoute>();
                        } else {
                            tempList = routesByStop.get(stopTitle);
                        }
                        tempList.add(new BusRoute(routeTitle, routeDescription));
                        count++;
                        routesByStop.put(stopTitle.trim(), tempList);
                    }

                    mRoutes.add(new BusRoute(routeTitle, routeDescription, routeStops));
                }
            } catch (JSONException ignored) {

            }

            mAdapter = new NewExpListViewAdapter(mDistanceArr, routesByStop, mActivity);
            mAdapter.setInflater((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE), mActivity);
            mExpLV.setAdapter(mAdapter);
        }
    }
}
