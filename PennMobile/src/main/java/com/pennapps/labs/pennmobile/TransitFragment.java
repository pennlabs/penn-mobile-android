package com.pennapps.labs.pennmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pennapps.labs.pennmobile.adapters.RoutesAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.MapCallbacks;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class TransitFragment extends Fragment {

    private MapView mapView;
    private GoogleMap googleMap;
    private SearchView searchView;
    private String query;
    private Labs mLabs;
    private EditText startingLoc;
    private static MapCallbacks mapCallBacks;
    private RoutesAdapter adapter;
    private MainActivity activity;
    static List<BusRoute> routes;
    public static HashSet<BusRoute> selectedRoutes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        mLabs = activity.getLabsInstance();
        selectedRoutes = new HashSet<>();

        mapCallBacks = MapFragment.getMapCallbacks();
        if (mapCallBacks == null) {
            mapCallBacks = new MapCallbacks();
            Context context = activity.getApplicationContext();
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(mapCallBacks)
                    .addOnConnectionFailedListener(mapCallBacks)
                    .addApi(LocationServices.API)
                    .build();
            mapCallBacks.setGoogleApiClient(mGoogleApiClient);
        }
        activity.closeKeyboard();
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
            MapsInitializer.initialize(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng myLocation = mapCallBacks.getLatLng();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
        startingLoc = (EditText) v.findViewById(R.id.transit_starting_location);
        startingLoc.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean nonEmptyQuery = query != null && !query.isEmpty();
                boolean inputFinished = actionId == EditorInfo.IME_ACTION_DONE || event != null;
                if (inputFinished && nonEmptyQuery) {
                    drawUserRoute(v.getEditableText().toString(), query);
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
    public void onStart() {
        super.onStart();
        mapCallBacks.getGoogleApiClient().connect();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        activity.setTitle(R.string.transit);
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
            case R.id.transit_route:
                if (adapter == null) {
                    loadRouteAdapter();
                } else {
                    showRouteDialogBox();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadRouteAdapter() {
        mLabs.routes().observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<BusRoute>>() {
            @Override
            public List<BusRoute> call(Throwable throwable) {
                return null;
            }
        }).subscribe(new Action1<List<BusRoute>>() {
            @Override
            public void call(List<BusRoute> routes) {
                TransitFragment.routes = routes;
                selectedRoutes.addAll(routes);
                adapter = new RoutesAdapter(activity.getApplicationContext(), routes);
                showRouteDialogBox();
            }
        });
    }

    private void showRouteDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.routes_list).setAdapter(adapter, null)
                .setPositiveButton(R.string.routes_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        googleMap.clear();
                        for (BusRoute busRoute : selectedRoutes) {
                            drawOfficialRoute(busRoute);
                        }
                    }
                }).show();
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
                if (arg0.isEmpty()) {
                    startingLoc.setVisibility(View.GONE);
                    startingLoc.setText("");
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                query = arg0;
                drawUserRoute(null, query);
                startingLoc.setVisibility(View.VISIBLE);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private LatLng getLocationLatLng(List<Building> buildings, String locationName) {
        if (buildings != null && !buildings.isEmpty()) {
            return buildings.get(0).getLatLng();
        }
        Geocoder geocoder = new Geocoder(activity.getApplicationContext());
        try {
            List<Address> locations = geocoder.getFromLocationName(locationName, 1);
            if (locations.size() == 0) {
                Toast.makeText(activity.getApplicationContext(),
                        R.string.location_not_found, Toast.LENGTH_SHORT).show();
                searchView.setQuery("", false);
                return null;
            }
            return new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void drawUserRoute(final String begin, final String dest) {
        activity.closeKeyboard();
        mLabs.buildings(dest)
                .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<Building>>() {
            @Override
            public List<Building> call(Throwable throwable) {
                return null;
            }
        }).subscribe(new Action1<List<Building>>() {
            @Override
            public void call(List<Building> buildings) {
                final LatLng destLatLng = getLocationLatLng(buildings, dest);
                if (destLatLng == null) {
                    return;
                }
                if (begin == null) {
                    retrieveRoute(mapCallBacks.getLatLng(), destLatLng, true);
                    return;
                }
                mLabs.buildings(begin)
                        .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<Building>>() {
                    @Override
                    public List<Building> call(Throwable throwable) {
                        return null;
                    }
                }).subscribe(new Action1<List<Building>>() {
                    @Override
                    public void call(List<Building> buildings) {
                        LatLng startLatLng = getLocationLatLng(buildings, begin);
                        if (startLatLng == null) {
                            return;
                        }
                        retrieveRoute(startLatLng, destLatLng, false);
                    }
                });
            }
        });
    }

    private void retrieveRoute(final LatLng startLatLng, final LatLng destLatLng, final boolean showCurrent) {
        String latBegin = String.valueOf(startLatLng.latitude);
        String longBegin = Double.toString(startLatLng.longitude);
        String latEnd = String.valueOf(destLatLng.latitude);
        String longEnd = Double.toString(destLatLng.longitude);
        mLabs.routing(latBegin, latEnd, longBegin, longEnd)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, BusRoute>() {
                    @Override
                    public BusRoute call(Throwable throwable) {
                        return null;
                    }
                })
                .subscribe(new Action1<BusRoute>() {
                    @Override
                    public void call(BusRoute route) {
                        googleMap.clear();
                        if (route == null || route.stops.size() == 0) {
                            Toast.makeText(activity.getApplicationContext(), R.string.no_path_found, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        PolylineOptions options = new PolylineOptions();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for (BusStop busStop : route.stops) {
                            LatLng latLngBuff = busStop.getLatLng();
                            addMapMarker(route, busStop, latLngBuff);
                            options.add(latLngBuff);
                            builder.include(latLngBuff);
                        }
                        builder.include(startLatLng);
                        builder.include(destLatLng);
                        options.width(15).color(Color.BLUE);

                        googleMap.addPolyline(options);
                        if (showCurrent) {
                            LatLng currentLocation = mapCallBacks.getLatLng();
                            builder.include(currentLocation);
                            addWalkingPath(currentLocation, route.stops.get(0));
                        }
                        addWalkingPath(destLatLng, route.stops.get(route.stops.size() - 1));
                        MapFragment.changeZoomLevel(googleMap, builder.build());
                    }
                });
    }

    private static class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomWindowAdapter(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.busstop_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker arg0) {
            TextView name = (TextView) view.findViewById(R.id.bus_stop_name);
            name.setText(arg0.getTitle());
            return view;
        }
    }

    private void addMapMarker(BusRoute route, BusStop busStop, LatLng latLngBuff) {
        if (busStop.getName() != null) {
            if (route.stops.indexOf(busStop) != 0
                    && route.stops.indexOf(busStop) != route.stops.size() - 1) {
                googleMap.addMarker(new MarkerOptions()
                        .position(latLngBuff)
                        .title(busStop.getName())
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_brightness_1_black_18dp)));
            } else {
                googleMap.addMarker(new MarkerOptions()
                        .position(latLngBuff)
                        .title(busStop.getName()));
            }
        }
    }

    private void addWalkingPath(LatLng startWalking, BusStop busStop) {
        PolylineOptions walkingPath = new PolylineOptions();
        walkingPath.add(busStop.getLatLng());
        walkingPath.add(startWalking);
        walkingPath.color(Color.RED).width(15);
        googleMap.addPolyline(walkingPath);
    }

    public static void toggleRouteSelection(BusRoute busRoute) {
        if (selectedRoutes.contains(busRoute)) {
            selectedRoutes.remove(busRoute);
        } else {
            selectedRoutes.add(busRoute);
        }
    }

    private void drawOfficialRoute(BusRoute busRoute) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions options = busRoute.getPolylineOptions();
        for (MarkerOptions markerOptions : busRoute.markers) {
            googleMap.addMarker(markerOptions);
        }
        for (BusStop busStop : busRoute.stops) {
            for (BusStop bs : busStop.path_to) {
                builder.include(bs.getLatLng());
            }
            builder.include(busStop.getLatLng());
        }
        for (BusStop bs : busRoute.stops.get(0).path_to) {
            builder.include(bs.getLatLng());
        }
        googleMap.addPolyline(options);
        MapFragment.changeZoomLevel(googleMap, builder.build());
    }
}
