package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MapFragment extends Fragment {

    private Labs mLabs;
    private MapView mapView;
    private GoogleMap googleMap;
    private SearchView searchView;
    private String query = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = ((MainActivity) getActivity()).getLabsInstance();

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            boolean seen = false;

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker arg0) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);

                ImageView imageView= (ImageView) view.findViewById(R.id.building_image);
                TextView name = (TextView) view.findViewById(R.id.building_name);
                name.setText(arg0.getTitle());

                if (arg0.getSnippet().isEmpty()) {
                    imageView.setVisibility(View.GONE);
                } else if (seen) {
                    Picasso.with(getActivity()).load(arg0.getSnippet()).into(imageView);
                } else {
                    seen = true;
                    Picasso.with(getActivity()).load(arg0.getSnippet()).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            getInfoContents(arg0);
                        }

                        @Override
                        public void onError() {}
                    });
                }
                return view;
            }
        });

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location location = googleMap.getMyLocation();
        LatLng myLocation = new LatLng(39.9529, -75.197098);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.building_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.building_search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.building, menu);

        searchView = (SearchView) menu.findItem(R.id.building_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                query = arg0;
                new GetRequestTask().execute();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private List<Building> buildings;

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = true;
            try {
                buildings = mLabs.buildings(query);
            } catch (Exception ignored) {
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
                googleMap.clear();
                if (!buildings.isEmpty()) {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (Building building : buildings) {
                        double latitude = Double.parseDouble(building.latitude);
                        double longitude = Double.parseDouble(building.longitude);
                        LatLng point = new LatLng(latitude, longitude);
                        boundsBuilder.include(point);
                        googleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(building.title)
                                .snippet(building.getImageURL()));
                    }
                    LatLngBounds bounds = boundsBuilder.build();
                    Location NECorner = new Location("");
                    Location SWCorner = new Location("");
                    LatLng northeast = bounds.northeast;
                    LatLng southwest = bounds.southwest;
                    NECorner.setLatitude(northeast.latitude);
                    NECorner.setLatitude(northeast.longitude);
                    SWCorner.setLatitude(southwest.latitude);
                    SWCorner.setLatitude(southwest.longitude);
                    int padding = 100;
                    if (SWCorner.distanceTo(NECorner) < 40) {
                        padding = 500;
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No results found.",
                            Toast.LENGTH_LONG).show();
                }
                searchView.clearFocus();
            } catch (NullPointerException ignored) {

            }
        }
    }
}
