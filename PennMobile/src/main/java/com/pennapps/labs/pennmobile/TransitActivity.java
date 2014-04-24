package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TransitActivity extends Activity {

    private TransitAPI mAPI;
    private ArrayList<BusStop> mTransitArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        new GetRequestTask(39.952960, -75.201339).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transit, menu);
        return true;
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {

        private JSONArray responseArr;
        private double latitude;
        private double longitude;

        GetRequestTask(double latitude, double longitude) {
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

                for (int i = 0; i < distanceArr.size(); i++) {
                    Log.v("vivlabs", distanceArr.get(i).getName() + " " + distanceArr.get(i).getDistance());
                }
            } catch (JSONException e) {
                Log.v("vivlabs", "" + e);
            }
        }
    }

}
