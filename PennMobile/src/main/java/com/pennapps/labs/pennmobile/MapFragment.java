package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.pennapps.labs.pennmobile.adapters.SearchSuggestionAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.MapCallbacks;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MapFragment extends Fragment {

    private Labs mLabs;
    private MapView mapView;
    private GoogleMap googleMap;

    private SearchView searchView;
    private String query;
    private MainActivity activity;

    @Bind(R.id.map_initial_card) LinearLayout initCard;
    @Bind(R.id.map_search_card) LinearLayout searchCard;
    @Bind(R.id.map_search_name) TextView searchName;
    @Bind(R.id.map_search_location) TextView searchLoc;
    @Bind(R.id.map_suggestion) ListView suggestionList;
    private SearchSuggestionAdapter adapter;
    private Building currentBuilding;
    private Marker currentMarker;
    private Set<Circle> displayCircles;
    private Set<Building> searchedBuildings;
    private Set<Marker> loadedMarkers;
    private static MapCallbacks mapCallbacks;

    private BuildingWindowAdapter buildingAdpater;
    private TransitWindowAdapter transitAdapter;

    private LatLng startQueryLatLng;
    private Polyline startPolyline;
    private Set<Polyline> allStartPolylines;

    private LatLng endQueryLatLng;
    private Polyline endPolyline;
    private Set<Polyline> allEndPolylines;
    private Semaphore semaphore;

    private RelativeLayout menuExtension;
    private EditText from, to;
    private ImageButton swap;

    private GeoApiContext geoapi;
    private final static int CIRCLE_SIZE = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        activity = (MainActivity) getActivity();
        displayCircles = new HashSet<>();
        loadedMarkers = new HashSet<>();
        mapCallbacks = new MapCallbacks();
        Context context = activity.getApplicationContext();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(mapCallbacks)
                .addOnConnectionFailedListener(mapCallbacks)
                .addApi(LocationServices.API)
                .build();
        mapCallbacks.setGoogleApiClient(mGoogleApiClient);
        activity.closeKeyboard();

        geoapi = new GeoApiContext().setApiKey(getString(R.string.google_api_key));
        allStartPolylines = new HashSet<>();
        allEndPolylines = new HashSet<>();
        query = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        ButterKnife.bind(this, v);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            //No permission -> go back to main
            Toast.makeText(activity, "Permission not granted for using Google Map", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.laundry_fragment, new MainFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                boolean changed = false;
                for (Circle c : displayCircles) {
                    float[] results = new float[1];
                    Location.distanceBetween(latLng.latitude, latLng.longitude, c.getCenter().latitude, c.getCenter().longitude, results);
                    if (results[0] <= CIRCLE_SIZE) {
                        changed = true;
                        displayCircles.remove(c);
                        c.remove();
                        break;
                    }
                }
                if (!changed) {
                    return;
                }
                for (Building b : searchedBuildings) {
                    float[] results = new float[1];
                    Location.distanceBetween(latLng.latitude, latLng.longitude, b.getLatLng().latitude, b.getLatLng().longitude, results);
                    if (results[0] <= CIRCLE_SIZE) {
                        currentBuilding = b;
                    }
                }
                displayCircles.add(googleMap.addCircle(new CircleOptions()
                        .center(currentMarker.getPosition())
                        .visible(true)
                        .radius(CIRCLE_SIZE)
                        .fillColor(Color.RED)
                        .strokeWidth(0)));
                currentMarker.remove();
                currentMarker = googleMap.addMarker(new MarkerOptions()
                        .position(currentBuilding.getLatLng())
                        .title(currentBuilding.title)
                        .snippet(currentBuilding.getImageURL()));
            }
        });
        buildingAdpater = new BuildingWindowAdapter(inflater);
        transitAdapter = new TransitWindowAdapter(inflater);
        googleMap.setInfoWindowAdapter(buildingAdpater);

        try {
            MapsInitializer.initialize(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCallbacks.getLatLng(), 14));

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (polyline.equals(startPolyline) || polyline.equals(endPolyline)) {
                    //this is ignored because the current polyline is already selected
                    return;
                }
                if (allStartPolylines.contains(polyline)) {
                    startPolyline.setColor(Color.GRAY);
                    startPolyline = polyline;
                } else if (allEndPolylines.contains(polyline)) {
                    endPolyline.setColor(Color.GRAY);
                    endPolyline = polyline;
                }
                polyline.setColor(Color.BLUE);
            }
        });

        menuExtension = activity.getMenuMapExtension();

        from = (EditText) menuExtension.findViewById(R.id.menu_map_from);
        to = (EditText) menuExtension.findViewById(R.id.menu_map_to);
        swap = (ImageButton) menuExtension.findViewById(R.id.menu_map_swap);

        v.findViewById(R.id.map_direction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openMapDirectionMenu();
                to.setText(query);
                googleMap.setInfoWindowAdapter(transitAdapter);
                ((FloatingActionButton) v).setImageResource(R.drawable.ic_directions_bus_white_24dp);
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable fromText = from.getText();
                from.setText(to.getText());
                to.setText(fromText);
            }
        });

        menuExtension.findViewById(android.R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.closeMapDirectionMenu();
            }
        });

        from.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    to.requestFocus();
                }
                return false;
            }
        });

        to.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    drawUserRoute(from.getText().toString(), to.getText().toString());
                }
                return false;
            }
        });
        return v;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapCallbacks.getGoogleApiClient().connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapCallbacks.requestLocationUpdates();
        activity.setTitle(R.string.map);
        activity.setNav(R.id.nav_map);
    }

    @Override
    public void onStop(){
        super.onStop();
        mapCallbacks.stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapCallbacks.stopLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    public static MapCallbacks getMapCallbacks(){
        return mapCallbacks;
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
                searchView.clearFocus();
                query = arg0;
                SearchFavoriteFragment.addSearchQuery(R.string.map_search_count, R.array.previous_map_array, query, activity, true);
                searchBuildings(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
        final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSuggestion();
                }
            }
        };
        searchView.setOnQueryTextFocusChangeListener(focusListener);

        final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                suggestionList.setVisibility(View.GONE);
                return false;
            }
        };
        searchView.setOnCloseListener(closeListener);
    }

    private void showSuggestion() {
        final ArrayList<String> list = new ArrayList<>(5);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        int index = sharedPref.getInt(getString(R.string.map_search_count), -1);
        if (index != -1) {
            String[] previousKey = getResources().getStringArray(R.array.previous_map_array);
            for (int i = 0; i < SearchFavoriteFragment.MAX_SUGGESTION_SIZE; i++){
                int id = (index + SearchFavoriteFragment.MAX_SUGGESTION_SIZE - i) % SearchFavoriteFragment.MAX_SUGGESTION_SIZE;
                String previous = sharedPref.getString(previousKey[id], "");
                if (!previous.isEmpty()) {
                    list.add(previous);
                }
            }
        }
        if (!list.isEmpty() && suggestionList != null) {
            suggestionList.setVisibility(View.VISIBLE);
            adapter = new SearchSuggestionAdapter(activity, list);
            suggestionList.setAdapter(adapter);
            suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    suggestionList.setVisibility(View.GONE);
                    searchView.setQuery(adapter.getItem(position), true);
                }
            });
        }
    }

    private void searchBuildings(String query) {
        mLabs.buildings(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Building>>() {
                               @Override
                               public void call(List<Building> buildings) {
                                   searchedBuildings = new HashSet<>(buildings);
                                   drawBuildingResults(buildings);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                activity.showErrorToast(R.string.location_not_found);
                            }
                        });
    }

    private void drawBuildingResults(List<Building> buildings) {
        googleMap.clear();
        if (buildings.isEmpty()) {
            activity.showErrorToast(R.string.location_not_found);
            return;
        }
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        currentBuilding = buildings.remove(0);
        currentMarker = googleMap.addMarker(new MarkerOptions()
                .position(currentBuilding.getLatLng())
                .title(currentBuilding.title)
                .snippet(currentBuilding.getImageURL()));
        for (Building building : buildings) {
            boundsBuilder.include(building.getLatLng());
            displayCircles.add(googleMap.addCircle(new CircleOptions()
                    .center(building.getLatLng())
                    .visible(true)
                    .radius(CIRCLE_SIZE)
                    .fillColor(Color.RED)
                    .strokeWidth(0)));
        }
        changeZoomLevel(googleMap, boundsBuilder.build());
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.cardscaleloc);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                initCard.removeAllViews();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                initCard.setVisibility(View.GONE);
                searchCard.setVisibility(View.VISIBLE);
                searchName.setText(currentBuilding.title);
                searchLoc.setText(currentBuilding.address);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        initCard.startAnimation(animation);
        suggestionList.setVisibility(View.GONE);
    }

    private void drawUserRoute(final String from, final String to) {
        activity.closeKeyboard();
        startQueryLatLng = null;
        endQueryLatLng = null;
        semaphore = new Semaphore(1);
        query = to;
        mLabs.buildings(to).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<List<Building>>() {
                    @Override
                    public void call(List<Building> buildings) {
                        final LatLng destLatLng = getLocationLatLng(buildings, to);
                        if (destLatLng == null) {
                            activity.showErrorToast(R.string.no_destination_found);
                        } else {
                            endQueryLatLng = destLatLng;
                            getRouteWithLoc();
                        }
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        activity.showErrorToast(R.string.no_destination_found);
                    }
                });
        if (from.equals(getString(R.string.starting_location_hint)) || from.equals("")) {
            startQueryLatLng = mapCallbacks.getLatLng();
            getRouteWithLoc();
        } else {
            mLabs.buildings(from).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Action1<List<Building>>() {
                        @Override
                        public void call(List<Building> buildings) {
                            LatLng startLatLng = getLocationLatLng(buildings, from);
                            if (startLatLng == null) {
                                activity.showErrorToast(R.string.no_origin_found);
                            } else {
                                startQueryLatLng = startLatLng;
                                getRouteWithLoc();
                            }
                        }
                    },
                    new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            activity.showErrorToast(R.string.no_origin_found);
                        }
                    }
            );
        }
    }

    public void getRouteWithLoc() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (endQueryLatLng == null || startQueryLatLng == null) {
            semaphore.release();
            return;
        }
        semaphore.release();
        retrieveRoute(startQueryLatLng, endQueryLatLng);
    }

    private void retrieveRoute(final LatLng startLatLng, final LatLng destLatLng) {
        String latBegin = String.valueOf(startLatLng.latitude);
        String longBegin = Double.toString(startLatLng.longitude);
        String latEnd = String.valueOf(destLatLng.latitude);
        String longEnd = Double.toString(destLatLng.longitude);
        mLabs.routing(latBegin, latEnd, longBegin, longEnd).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<BusRoute>() {
                            @Override
                            public void call(BusRoute route) {
                                googleMap.clear();
                                displayCircles.clear();
                                searchedBuildings.clear();
                                loadedMarkers.clear();
                                searchLoc.setText("");
                                if (route == null || route.stops.size() == 0) {
                                    addWalkingPath(startLatLng, destLatLng, true);
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
                                addWalkingPath(startLatLng, route.stops.get(0).getLatLng(), true);
                                addWalkingPath(destLatLng, route.stops.get(route.stops.size() - 1).getLatLng(), false);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(destLatLng)
                                        .title(query));
                                changeZoomLevel(googleMap, builder.build());
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                googleMap.clear();
                                displayCircles.clear();
                                searchedBuildings.clear();
                                loadedMarkers.clear();
                                searchLoc.setText("");
                                activity.showErrorToast(R.string.no_bus_route);
                                addWalkingPath(startLatLng, destLatLng, true);
                            }
                        });
    }


    private LatLng getLocationLatLng(List<Building> buildings, String locationName) {
        if (buildings != null && !buildings.isEmpty()) {
            return buildings.get(0).getLatLng();
        }
        Geocoder geocoder = new Geocoder(activity.getApplicationContext());
        try {
            List<Address> locations = geocoder.getFromLocationName(locationName, 1);
            if (locations.size() != 0) {
                return new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        activity.showErrorToast(R.string.no_path_found);
        return null;
    }

    private void addWalkingPath(LatLng startWalking, LatLng endWalking, final boolean start) {
        StringBuilder from = new StringBuilder();
        from.append(startWalking.latitude).append(',').append(startWalking.longitude);
        StringBuilder to = new StringBuilder();
        to.append(endWalking.latitude).append(',').append(endWalking.longitude);
        DirectionsApiRequest req = DirectionsApi.getDirections(geoapi, from.toString(), to.toString());
        req.mode(TravelMode.WALKING);
        req.setCallback(new PendingResult.Callback<DirectionsResult>() {

            @Override
            public void onResult(final DirectionsResult result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DirectionsRoute[] routes = result.routes;
                        EncodedPolyline polyline = routes[0].overviewPolyline;
                        PolylineOptions options = new PolylineOptions();
                        for (com.google.maps.model.LatLng latLng : polyline.decodePath()) {
                            options.add(new LatLng(latLng.lat, latLng.lng));
                        }
                        options.color(Color.BLUE).width(15);
                        Polyline line = googleMap.addPolyline(options);
                        if (start) {
                            startPolyline = line;
                            allStartPolylines.add(startPolyline);
                        } else {
                            endPolyline = line;
                            allEndPolylines.add(endPolyline);
                        }
                        for (int i = 1; i < routes.length; i++) {
                            PolylineOptions opt = new PolylineOptions();
                            for (com.google.maps.model.LatLng latLng : routes[i].overviewPolyline.decodePath()) {
                                opt.add(new LatLng(latLng.lat, latLng.lng));
                            }
                            opt.color(Color.GRAY).width(15);
                            Polyline poly = googleMap.addPolyline(opt);
                            if (start) {
                                allStartPolylines.add(poly);
                            } else {
                                allEndPolylines.add(poly);
                            }
                        }
                        StringBuilder builder = new StringBuilder(searchLoc.getText());
                        for (DirectionsRoute r : result.routes) {
                            if (!builder.toString().contains(r.copyrights)) {
                                if (builder.length() > 0) {
                                    builder.append(" ");
                                }
                                builder.append(r.copyrights);
                            }
                        }
                        boolean added = false;
                        for (DirectionsRoute r : result.routes) {
                            for (String s : r.warnings) {
                                if (!builder.toString().contains(s)) {
                                    if (!added) {
                                        added = true;
                                        builder.append("\n");
                                    }
                                    builder.append(s);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                activity.showErrorToast(R.string.google_map_fail);
            }
        });
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
    public static void changeZoomLevel(GoogleMap googleMap, LatLngBounds bounds){
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
    }

    private class BuildingWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public BuildingWindowAdapter(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            currentMarker = marker;

            ImageView imageView = (ImageView) view.findViewById(R.id.building_image);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.mapProgress);
            TextView name = (TextView) view.findViewById(R.id.building_name);
            name.setText(marker.getTitle());

            if (marker.getSnippet().isEmpty()) {
                imageView.setVisibility(View.GONE);
            } else if (loadedMarkers.contains(currentMarker)) {
                Picasso.with(activity).load(marker.getSnippet()).fit().centerInside().into(imageView);
            } else {
                loadedMarkers.add(currentMarker);
                progressBar.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(marker.getSnippet()).fit().centerInside().into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
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

    private static class TransitWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public TransitWindowAdapter(LayoutInflater inflater) {
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
}
