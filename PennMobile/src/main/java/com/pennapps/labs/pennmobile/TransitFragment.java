package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;

import java.io.IOException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class TransitFragment extends Fragment {

    private MapView mapView;
    private GoogleMap googleMap;
    private SearchView searchView;
    private String query = "";
    private Labs mLabs;
    private GoogleApiClient mGoogleApiClient;
    private static MapFragment.MapCallBacks mapCallBacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLabs = ((MainActivity) getActivity()).getLabsInstance();

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mapCallBacks = MapFragment.getMapCallBacks();
        if(mapCallBacks == null) {
            mapCallBacks = new MapFragment.MapCallBacks();
            mGoogleApiClient = new GoogleApiClient.Builder((getActivity().getApplicationContext()))
                    .addConnectionCallbacks(mapCallBacks)
                    .addOnConnectionFailedListener(mapCallBacks)
                    .addApi(LocationServices.API)
                    .build();
            mapCallBacks.setGoogleApiClient(mGoogleApiClient);
        } else{
            mGoogleApiClient = mapCallBacks.getGoogleApiClient();
        }

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transit, container, false);


        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng myLocation = mapCallBacks.getLatLng();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
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
            case R.id.transit_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.transit_search).getActionView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transit, menu);

        searchView = (SearchView) menu.findItem(R.id.transit_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                query = arg0;
                searchTransit(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    public LatLng getLatLng(String destination) {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            List<Address> locationList = geocoder.getFromLocationName(destination, 1);
            if (locationList.size() > 0) {
                return new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void searchTransit(String query) {
        LatLng latLng = getLatLng(query);
        Toast.makeText(getActivity().getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
        mLabs.routing(String.valueOf(mapCallBacks.getLatLng().latitude), Double.toString(latLng.latitude),
                String.valueOf(mapCallBacks.getLatLng().longitude), Double.toString(latLng.longitude))
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn(new Func1<Throwable, BusRoute>() {
                @Override
                public BusRoute call(Throwable throwable) {
                    return new BusRoute();
                }
            })
            .subscribe(new Action1<BusRoute>() {
                @Override
                public void call(BusRoute route) {
                    googleMap.clear();
                    PolylineOptions options = new PolylineOptions();
                    for (BusStop busStop : route.path) {
                        LatLng latLngBuff = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                        googleMap.addCircle(new CircleOptions()
                                .center(latLngBuff)
                                .radius(10));
                        options.add(latLngBuff);
                    }
                    options.width(5).color(Color.BLACK);
                    Polyline line = googleMap.addPolyline(options);
                }
            });
    }
}