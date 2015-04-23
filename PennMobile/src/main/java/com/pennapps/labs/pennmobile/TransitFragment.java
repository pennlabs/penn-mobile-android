package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusPath;
import com.pennapps.labs.pennmobile.classes.BusStop;

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
    private EditText startingLoc;
    private static MapFragment.MapCallBacks mapCallBacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLabs = ((MainActivity) getActivity()).getLabsInstance();

        mapCallBacks = MapFragment.getMapCallBacks();
        if(mapCallBacks == null) {
            mapCallBacks = new MapFragment.MapCallBacks();
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder((getActivity().getApplicationContext()))
                    .addConnectionCallbacks(mapCallBacks)
                    .addOnConnectionFailedListener(mapCallBacks)
                    .addApi(LocationServices.API)
                    .build();
            mapCallBacks.setGoogleApiClient(mGoogleApiClient);
        }
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

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
        googleMap.setInfoWindowAdapter(new CustomWindowAdapter(inflater));

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng myLocation = mapCallBacks.getLatLng();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
        startingLoc = (EditText) v.findViewById(R.id.transit_starting_location);
        startingLoc.setOnEditorActionListener(new OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((actionId == EditorInfo.IME_ACTION_DONE || event != null) && query != null && !query.isEmpty()){
                    drawMap(query, v.getEditableText().toString(), null);
                }
                return false;
            }
        });
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
                if(arg0.isEmpty()){
                    startingLoc.setVisibility(View.GONE);
                    startingLoc.setText("");
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                query = arg0;
                drawMap(query, null, mapCallBacks.getLatLng());
                startingLoc.setVisibility(View.VISIBLE);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    public void drawMap(String destination, String start, LatLng current) {
        final String begin = start;
        final LatLng beginL = current;
        final String dest = destination;
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String q = destination;
        if(current == null){
            q = start;
        }
        mLabs.buildings(q)
                .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<Building>>() {
            @Override
            public List<Building> call(Throwable throwable) {
                return null;
            }
        }).subscribe(new Action1<List<Building>>() {
            @Override
            public void call(List<Building> buildings) {
                LatLng latLng = null;
                if (!buildings.isEmpty()) {
                    latLng = new LatLng(Double.parseDouble(buildings.get(0).latitude),
                            Double.parseDouble(buildings.get(0).longitude));
                    Toast.makeText(getActivity().getApplicationContext(),
                            "latLng = " + latLng, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No results found.",
                            Toast.LENGTH_LONG).show();
                    Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    try {
                        List<Address> locationList = geocoder.getFromLocationName(dest, 1);
                        if (locationList.size() > 0) {
                            latLng = new LatLng(locationList.get(0).getLatitude(),
                                    locationList.get(0).getLongitude());
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Location not found, please try again", Toast.LENGTH_SHORT).show();
                            searchView.setQuery("", false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(beginL == null){
                    drawMap(dest, begin, latLng);
                }
                else {
                    final LatLng endL = latLng;
                    Toast.makeText(getActivity().getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
                    mLabs.routing(String.valueOf(beginL.latitude), Double.toString(endL.latitude),
                            String.valueOf(beginL.longitude), Double.toString(endL.longitude))
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(new Func1<Throwable, BusPath>() {
                                @Override
                                public BusPath call(Throwable throwable) {
                                    return new BusPath();
                                }
                            })
                            .subscribe(new Action1<BusPath>() {
                                @Override
                                public void call(BusPath route) {
                                    googleMap.clear();
                                    if (route == null) {
                                        Toast.makeText(getActivity().getApplicationContext(), "No path found.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "route found", Toast.LENGTH_SHORT).show();
                                    PolylineOptions options = new PolylineOptions();
                                    for (BusStop busStop : route.path) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                busStop.getLatitude() + " " + busStop.getLongitude(), Toast.LENGTH_SHORT).show();
                                        LatLng latLngBuff = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        googleMap.addMarker(new MarkerOptions()
                                                        .position(latLngBuff)
                                                        .title(busStop.getName()));
                                        options.add(latLngBuff);
                                    }
                                    options.width(15).color(Color.BLUE);
                                    googleMap.addPolyline(options);
                                    PolylineOptions startwalk = new PolylineOptions();
                                    startwalk.add(beginL);
                                    startwalk.add(new LatLng(route.path.get(0).getLatitude(), route.path.get(0).getLongitude()));
                                    startwalk.color(Color.RED).width(15);
                                    googleMap.addPolyline(startwalk);
                                    PolylineOptions endwalk = new PolylineOptions();
                                    endwalk.add(new LatLng(route.path.get(route.path.size() - 1).getLatitude(), route.path.get(route.path.size() - 1).getLongitude()));
                                    endwalk.add(endL);
                                    endwalk.color(Color.RED).width(15);
                                    googleMap.addPolyline(endwalk);
                                }
                            });
                }
            }
        });
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
            TextView name = (TextView) view.findViewById(R.id.building_name);
            name.setText(arg0.getTitle());
            return view;
        }
    }
}