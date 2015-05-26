package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MapFragment extends Fragment {

    public static final String TAG = "MapFragment";
    private Labs mLabs;
    private MapView mapView;
    private GoogleMap googleMap;
    private SearchView searchView;
    private String query = "";
    private static Marker currentMarker;
    private static Set<Marker> loadedMarkers;
    private GoogleApiClient mGoogleApiClient;
    private static MapCallBacks mapCallBacks;
    public static final LatLng DEFAULT_LATLNG = new LatLng(39.9529, -75.197098);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        loadedMarkers = new HashSet<>();
        mapCallBacks = new MapCallBacks();
        mGoogleApiClient = new GoogleApiClient.Builder(( getActivity().getApplicationContext()))
                .addConnectionCallbacks(mapCallBacks)
                .addOnConnectionFailedListener(mapCallBacks)
                .addApi(LocationServices.API)
                .build();
        mapCallBacks.setGoogleApiClient(mGoogleApiClient);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).closeKeyboard();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new CustomWindowAdapter(inflater));

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCallBacks.getLatLng(), 14));


        return v;
    }
    private class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        LayoutInflater inflater = null;

        public CustomWindowAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
            view = inflater.inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker arg0) {
            currentMarker = arg0;

            ImageView imageView= (ImageView) view.findViewById(R.id.building_image);
            TextView name = (TextView) view.findViewById(R.id.building_name);
            name.setText(arg0.getTitle());

            if (arg0.getSnippet().isEmpty()) {
                imageView.setVisibility(View.GONE);
            } else if (loadedMarkers.contains(currentMarker)) {
                Picasso.with(getActivity()).load(arg0.getSnippet()).into(imageView);
            } else {
                loadedMarkers.add(currentMarker);
                Picasso.with(getActivity()).load(arg0.getSnippet()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        currentMarker.hideInfoWindow();
                        currentMarker.showInfoWindow();
                    }

                    @Override
                    public void onError() {}
                });
            }
            return view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        mapCallBacks.getGoogleApiClient().connect();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        mapCallBacks.requestLocationUpdates();
    }

    @Override
    public void onStop(){
        super.onStop();
        mapCallBacks.stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapCallBacks.stopLocationUpdates();
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

    public static MapCallBacks getMapCallBacks(){
        return mapCallBacks;
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
                searchBuildings(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private void searchBuildings(String query) {
        mLabs.buildings(query)
            .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<Building>>() {
            @Override
            public List<Building> call(Throwable throwable) {
                return null;
            }
        })
            .subscribe(new Action1<List<Building>>() {
            @Override
            public void call(List<Building> buildings) {
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
            }
            });
    }

    static class MapCallBacks implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener  {
        LatLng latLng;
        GoogleApiClient mGoogleApiClient;
        LocationRequest mLocationRequest;
        boolean waiting, called, connected;
        MapCallBacks(){
            createLocationRequest();
            called = false;
            connected = false;
            waiting = false;
        }

        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        @Override
        public void onConnected(Bundle bundle) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location == null){
                latLng = DEFAULT_LATLNG;
            }else{
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            Log.d(TAG, "new lat lng = " + latLng);
            requestLocationUpdates();
            waiting = false;
        }

        public void requestLocationUpdates(){
            if(!called && connected) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest,
                        (com.google.android.gms.location.LocationListener) this);
            }
            called = true;
        }

        public void stopLocationUpdates() {
            if(connected && called) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            }
            if(latLng == null) {
                latLng = DEFAULT_LATLNG;
            }
            called = false;
        }

        @Override
        public void onConnectionSuspended(int cause){
            //handle cause later
            if(latLng == null) {
                latLng = DEFAULT_LATLNG;
            }
            waiting = false;
            connected = false;
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            //handle connectionResult later
            if(latLng == null) {
                latLng = DEFAULT_LATLNG;
            }
            waiting = false;
        }

        public LatLng getLatLng(){
            if(latLng == null){
                return DEFAULT_LATLNG;
            }
            return latLng;
        }

        public GoogleApiClient getGoogleApiClient(){
            return mGoogleApiClient;
        }

        public void setGoogleApiClient(GoogleApiClient mGoogleApiClient){
            this.mGoogleApiClient = mGoogleApiClient;
        }

        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.AVAILABLE){
                waiting = false;
                requestLocationUpdates();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (waiting){
                requestLocationUpdates();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            stopLocationUpdates();
            waiting = true;
        }
    }
}
