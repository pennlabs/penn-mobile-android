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

public class TransitActivity extends Activity {

    private TransitAPI mAPI;
    private ArrayList<BusStop> mTransitArr;
    private double mLatitude;
    private double mLongitude;

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

        // Log.v("vivlabs", service.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString());
        Location mLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLatitude = mLocation.getLatitude();
        mLongitude = mLocation.getLongitude();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transit, menu);
        return true;
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {

        private JSONArray responseArr;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getCourse("");
                responseArr = (JSONArray) resultObj.get("result_data");
                if (responseArr.length() == 0) return false;
                return true;
            } catch (JSONException e) {
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
                JSONObject resp;

                for (int i = 0; i < responseArr.length(); i++) {
                    resp = (JSONObject) responseArr.get(i);
                    BusStop stop = new BusStop(resp.get("BusStopId").toString(),
                                               resp.get("BusStopName").toString(),
                                               resp.get("Latitude").toString(),
                                               resp.get("Longitude").toString());
                    mTransitArr.add(stop);
                }

                ArrayList<String> sortedArr = new ArrayList<String>();
                for (int i = 0; i < mTransitArr.size(); i++) {
                    BusStop currentStop = mTransitArr.get(i);
                    for (int j = 0; j < mTransitArr.size(); j++) {
                        
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

}
