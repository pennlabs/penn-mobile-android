package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.List;

public class MapFragment extends Fragment {

    private Labs mLabs;
    private MapView mapView;
    private GoogleMap googleMap;
    private Context mContext;
    private String mName;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        mName = getArguments().getString(DirectorySearchFragment.NAME_INTENT_EXTRA);

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        new GetRequestTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location location = googleMap.getMyLocation();
        LatLng myLocation = new LatLng(39.952702,-75.193497);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private List<Building> buildings;

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = true;
            try {
                buildings = mLabs.buildings(mName);
            } catch(Exception ignored) {
                ignored.printStackTrace();
                success = false;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                // TODO:
                return;
            }
            try {
                if (buildings.size() == 0) {
                    getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                } else {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (Building building : buildings) {
                        double latitude = Double.parseDouble(building.latitude);
                        double longitude = Double.parseDouble(building.longitude);
                        LatLng point = new LatLng(latitude, longitude);
                        boundsBuilder.include(point);
                        googleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(building.title));
                    }
                    LatLngBounds bounds = boundsBuilder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    getActivity().findViewById(R.id.no_results).setVisibility(View.GONE);
                    getActivity().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
                }
                searchView.clearFocus();
            } catch (NullPointerException ignored) {

            }
        }
    }
}
