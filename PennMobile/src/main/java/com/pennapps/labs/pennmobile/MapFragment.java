package com.pennapps.labs.pennmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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
import com.pennapps.labs.pennmobile.adapters.RoutesAdapter;
import com.pennapps.labs.pennmobile.adapters.SearchSuggestionAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.MapCallbacks;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MapFragment extends Fragment {

    private Labs mLabs;
    private MapView mapView;
    private GoogleMap googleMap;

    private SearchView searchView;
    private String query;
    private MainActivity activity;

    @Bind(R.id.map_initial_card)
    LinearLayout initCard;
    @Bind(R.id.map_search_card)
    LinearLayout searchCard;
    @Bind(R.id.map_search_name)
    TextView searchName;
    @Bind(R.id.map_search_location)
    TextView searchLoc;
    @Bind(R.id.map_suggestion)
    ListView suggestionList;
    @Bind(R.id.map_bus_card)
    TextView busStopName;
    private FloatingActionButton transitMode;
    private SearchSuggestionAdapter adapter;
    private Building currentBuilding;
    private Marker currentMarker;
    private Set<Circle> displayCircles;
    private Set<Building> searchedBuildings;
    private Set<Marker> loadedMarkers;
    private static MapCallbacks mapCallbacks;

    private Set<Circle> busStopCircles;
    private Set<BusStop> busStopAdded;
    private Circle currentBusStop;

    private BuildingWindowAdapter buildingAdpater;
    private TransitWindowAdapter transitAdapter;

    private LatLng startQueryLatLng;
    private Polyline startPolyline;
    private Set<Polyline> allStartPolylines;

    private LatLng endQueryLatLng;
    private Polyline endPolyline;
    private Set<Polyline> allEndPolylines;
    private Semaphore semaphore;

    private EditText from, to;

    private GeoApiContext geoapi;
    private boolean useBus, transit;
    private final static int CIRCLE_SIZE = 10;
    private final static double BOUND_EXPAND_CONSTANT = 0.004;
    private final static double SENSITIVITY_MULTIPLIER = 1.5;

    public static HashSet<BusRoute> selectedRoutes;
    private RoutesAdapter routesAdapter;

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

        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Maps")
                .putContentType("App Feature")
                .putContentId("4"));

        geoapi = new GeoApiContext().setApiKey(getString(R.string.google_api_key));
        allStartPolylines = new HashSet<>();
        allEndPolylines = new HashSet<>();
        query = null;
        transit = false;
        useBus = true;
        selectedRoutes = new HashSet<>();
        busStopCircles = new HashSet<>();
        busStopAdded = new HashSet<>();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        if (mapView == null) {
            v = inflater.inflate(R.layout.fragment_map_fail, container, false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            int numberOfClicks = sharedPref.getInt(getString(R.string.no_map_count_key), 0);
            if (numberOfClicks > 5) {
                ImageView iv = (ImageView) v.findViewById(R.id.no_map_iv);
                iv.setImageResource(R.drawable.device_no_map_meme);
                v.findViewById(R.id.no_map_tv).setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                iv.setLayoutParams(params);
            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.no_map_count_key), numberOfClicks + 1);
                editor.apply();
            }
            return v;
        }
        mapView.onCreate(savedInstanceState);

        ButterKnife.bind(this, v);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {

            //No permission -> go back to main
            activity.showErrorToast(R.string.no_permission_map);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new MainFragment())
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
                    if (results[0] <= SENSITIVITY_MULTIPLIER * CIRCLE_SIZE) {
                        changed = true;
                        displayCircles.remove(c);
                        c.remove();
                        break;
                    }
                }
                if (!changed) {
                    checkBusStopClicked(latLng);
                    return;
                }
                for (Building b : searchedBuildings) {
                    float[] results = new float[1];
                    Location.distanceBetween(latLng.latitude, latLng.longitude, b.getLatLng().latitude, b.getLatLng().longitude, results);
                    if (results[0] <= SENSITIVITY_MULTIPLIER * CIRCLE_SIZE) {
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

        RelativeLayout menuExtension = activity.getMenuMapExtension();

        from = (EditText) menuExtension.findViewById(R.id.menu_map_from);
        to = (EditText) menuExtension.findViewById(R.id.menu_map_to);
        ImageButton swap = (ImageButton) menuExtension.findViewById(R.id.menu_map_swap);
        ImageButton useCurrent = (ImageButton) menuExtension.findViewById(R.id.menu_map_circle);
        transitMode = (FloatingActionButton) v.findViewById(R.id.map_direction);
        FloatingActionButton currentLocButton = (FloatingActionButton) v.findViewById(R.id.map_current_loc);

        transitMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton button = (FloatingActionButton) v;
                if (!transit) {
                    activity.openMapDirectionMenu();
                    to.setText(query);
                    googleMap.setInfoWindowAdapter(transitAdapter);
                    button.setImageResource(R.drawable.ic_directions_walk_white_24dp);
                    useBus = false;
                    transit = true;
                    drawUserRoute("", query);
                } else if (useBus) {
                    button.setImageResource(R.drawable.ic_directions_bus_white_24dp);
                    drawUserRoute(from.getText().toString(), to.getText().toString());
                } else {
                    button.setImageResource(R.drawable.ic_directions_walk_white_24dp);
                    drawUserRoute(from.getText().toString(), to.getText().toString());
                }
                useBus = !useBus;
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
                suggestionList.setVisibility(View.GONE);
                transit = false;
                transitMode.setImageResource(R.drawable.ic_directions_white_24dp);
                googleMap.setInfoWindowAdapter(buildingAdpater);
                googleMap.clear();
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.cardscalesmaller);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        searchName.setVisibility(View.GONE);
                        searchLoc.setVisibility(View.GONE);
                        initCard.setVisibility(View.VISIBLE);
                        searchCard.setVisibility(View.GONE);
                        busStopName.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        busStopName.setVisibility(View.VISIBLE);
                        busStopName.setText(currentBuilding == null ? "" : currentBuilding.title);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                initCard.startAnimation(animation);
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

        useCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from.setText("");
            }
        });

        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                boundsBuilder.include(mapCallbacks.getLatLng());
                changeZoomLevel(googleMap, boundsBuilder.build());
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
        if (mapView == null) {
            return;
        }
        mapView.onResume();
        mapCallbacks.requestLocationUpdates();
        activity.setTitle(R.string.map);
        activity.setNav(R.id.nav_map);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapCallbacks.stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView == null) {
            return;
        }
        mapView.onDestroy();
        mapCallbacks.stopLocationUpdates();
        activity.closeMapDirectionMenu();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        activity.closeMapDirectionMenu();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView == null) {
            return;
        }
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.building_search:
                return true;
            case R.id.transit_route:
                if (routesAdapter == null) {
                    loadRouteAdapter();
                } else {
                    showRouteDialogBox();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu != null && menu.findItem(R.id.building_search) != null) {
            searchView = (SearchView) menu.findItem(R.id.building_search).getActionView();
            searchView.setIconifiedByDefault(true);
            searchView.setIconified(true);
        }
    }

    public static MapCallbacks getMapCallbacks() {
        return mapCallbacks;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mapView == null) {
            return;
        }
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
                suggestionList.setVisibility(View.GONE);
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
        final ArrayList<String> list = new ArrayList<>(SearchFavoriteFragment.MAX_SUGGESTION_SIZE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        int index = sharedPref.getInt(getString(R.string.map_search_count), -1);
        if (index != -1) {
            String[] previousKey = getResources().getStringArray(R.array.previous_map_array);
            for (int i = 0; i < SearchFavoriteFragment.MAX_SUGGESTION_SIZE; i++) {
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
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.cardscalebigger);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                busStopName.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                initCard.setVisibility(View.GONE);
                searchCard.setVisibility(View.VISIBLE);
                searchName.setText(currentBuilding.title);
                searchLoc.setText(currentBuilding.address);
                searchName.setVisibility(View.VISIBLE);
                searchLoc.setVisibility(View.VISIBLE);
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
        if (to == null || to.isEmpty()) {
            query = getString(R.string.starting_location_hint);
            endQueryLatLng = mapCallbacks.getLatLng();
        } else {
            mLabs.buildings(to).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Action1<List<Building>>() {
                        @Override
                        public void call(List<Building> buildings) {
                            final LatLng destLatLng = getLocationLatLng(buildings, to);
                            currentBuilding = buildings != null && buildings.size() >= 1 ? buildings.get(0) : currentBuilding;
                            searchName.setText(currentBuilding.title);
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
        }
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
        if (useBus) {
            mLabs.routing(latBegin, latEnd, longBegin, longEnd).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<BusRoute>() {
                                @Override
                                public void call(BusRoute route) {
                                    if (route == null || route.stops.size() == 0) {
                                        addWalkingPath(startLatLng, destLatLng, true, true);
                                        return;
                                    }
                                    clearMap();
                                    PolylineOptions options = new PolylineOptions();
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                    for (BusStop busStop : route.stops) {
                                        LatLng latLngBuff = busStop.getLatLng();
                                        addMapMarker(route, busStop);
                                        options.add(latLngBuff);
                                        builder.include(latLngBuff);
                                    }
                                    builder.include(startLatLng);
                                    builder.include(destLatLng);
                                    options.width(15).color(getResources().getColor(R.color.color_primary));

                                    googleMap.addPolyline(options);
                                    addWalkingPath(startLatLng, route.stops.get(0).getLatLng(), true, false);
                                    addWalkingPath(destLatLng, route.stops.get(route.stops.size() - 1).getLatLng(), false, false);
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(destLatLng)
                                            .title(query));
                                    changeZoomLevel(googleMap, builder.build());
                                }
                            },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    findBusRoute(startLatLng, destLatLng);
                                }
                            });
        } else {
            addWalkingPath(startLatLng, destLatLng, true, true);
        }
    }

    private void clearMap() {
        googleMap.clear();
        displayCircles.clear();
        if (searchedBuildings != null) {
            searchedBuildings.clear();
        }
        loadedMarkers.clear();
        searchLoc.setText("");
        busStopCircles.clear();
        busStopAdded.clear();
        currentBusStop = null;
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

    private void addWalkingPath(final LatLng startWalking, final LatLng endWalking, final boolean start, final boolean clearOnSuccess) {
        DirectionsApiRequest req = DirectionsApi.getDirections(geoapi, startWalking.latitude + "," + startWalking.longitude,
                endWalking.latitude + "," + endWalking.longitude);
        req.mode(TravelMode.WALKING);
        req.setCallback(new PendingResult.Callback<DirectionsResult>() {

            @Override
            public void onResult(final DirectionsResult result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (clearOnSuccess) {
                            clearMap();
                        }
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
                        addGoogleWarning(result);
                        if (clearOnSuccess) {
                            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                            bounds.include(startWalking);
                            bounds.include(endWalking);
                            changeZoomLevel(googleMap, bounds.build());
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


    private void addMapMarker(BusRoute route, BusStop busStop) {
        if (busStop.getName() != null) {
            if (route.stops.indexOf(busStop) != 0
                    && route.stops.indexOf(busStop) != route.stops.size() - 1) {
                drawBusStop(busStop);
            } else {
                googleMap.addMarker(new MarkerOptions()
                        .position(busStop.getLatLng())
                        .title(busStop.getName()));
            }
        }
    }

    public static void changeZoomLevel(GoogleMap googleMap, LatLngBounds bounds) {
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
            bounds = bounds.including(new LatLng(northeast.latitude + BOUND_EXPAND_CONSTANT, northeast.longitude - BOUND_EXPAND_CONSTANT));
            bounds = bounds.including(new LatLng(southwest.latitude - BOUND_EXPAND_CONSTANT, southwest.longitude + BOUND_EXPAND_CONSTANT));
        }
        bounds = bounds.including(new LatLng(southwest.latitude - 0.0005, southwest.longitude));
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
                    public void onError() {
                    }
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

    private void findBusRoute(final LatLng startLatLng, final LatLng destLatLng) {
        mLabs.bus_stops().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<List<BusStop>>() {
                    @Override
                    public void call(List<BusStop> busStops) {
                        HashMap<String, TreeSet<BusStop>> map = new HashMap<>();
                        String west = getString(R.string.bus_west);
                        String east = getString(R.string.bus_east);
                        map.put(east, new TreeSet<>(new BusStopComparator(east)));
                        map.put(west, new TreeSet<>(new BusStopComparator(west)));
                        for (BusStop bs : busStops) {
                            generateBusRouteMap(bs);
                            for (Map.Entry<String, Integer> e : bs.routesMap.entrySet()) {
                                if (map.containsKey(e.getKey())) {
                                    map.get(e.getKey()).add(bs);
                                }
                            }
                        }
                        float[] res = new float[1];
                        BusStop eastStart = null, eastEnd = null, westStart = null, westEnd = null;
                        double minStartBuffer = Double.MAX_VALUE, minEndBuffer = Double.MAX_VALUE, total;
                        for (BusStop bs : map.get(east)) {
                            Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, bs.getLatLng().latitude, bs.getLatLng().longitude, res);
                            if (res[0] < minStartBuffer) {
                                minStartBuffer = res[0];
                                eastStart = bs;
                            }
                            Location.distanceBetween(destLatLng.latitude, destLatLng.longitude, bs.getLatLng().latitude, bs.getLatLng().longitude, res);
                            if (res[0] < minEndBuffer) {
                                minEndBuffer = res[0];
                                eastEnd = bs;
                            }
                        }
                        total = minStartBuffer + minEndBuffer;
                        minStartBuffer = Double.MAX_VALUE;
                        minEndBuffer = Double.MAX_VALUE;
                        for (BusStop bs : map.get(west)) {
                            Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, bs.getLatLng().latitude, bs.getLatLng().longitude, res);
                            if (res[0] < minStartBuffer) {
                                minStartBuffer = res[0];
                                westStart = bs;
                            }
                            Location.distanceBetween(destLatLng.latitude, destLatLng.longitude, bs.getLatLng().latitude, bs.getLatLng().longitude, res);
                            if (res[0] < minEndBuffer) {
                                minEndBuffer = res[0];
                                westEnd = bs;
                            }
                        }
                        DirectionsApiRequest req;
                        final LinkedList<BusStop> interStops = new LinkedList<>();
                        if (total > minStartBuffer + minEndBuffer) {
                            //west
                            if (westStart == null || westEnd == null) {
                                activity.showErrorToast(R.string.no_bus_route);
                                addWalkingPath(startLatLng, destLatLng, true, true);
                                return;
                            }
                            req = getGoogleBusRoute(map, west, westStart, westEnd, interStops);
                        } else {
                            //east
                            if (eastStart == null || eastEnd == null) {
                                activity.showErrorToast(R.string.no_bus_route);
                                addWalkingPath(startLatLng, destLatLng, true, true);
                                return;
                            }
                            req = getGoogleBusRoute(map, east, eastStart, eastEnd, interStops);
                        }
                        if (eastStart == null || westStart == null || (interStops.isEmpty() && eastStart.equals(eastEnd) && westStart.equals(westEnd))) {
                            activity.showErrorToast(R.string.no_bus_route);
                            addWalkingPath(startLatLng, destLatLng, true, true);
                            return;
                        }
                        final BusStop startStop = total > minStartBuffer + minEndBuffer ? westStart : eastStart;
                        final BusStop endStop = total > minStartBuffer + minEndBuffer ? westEnd : eastEnd;
                        req.mode(TravelMode.DRIVING).optimizeWaypoints(false);
                        req.setCallback(getBusRouteCallback(interStops, startStop, endStop, startLatLng, destLatLng));
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        addWalkingPath(startLatLng, destLatLng, true, true);
                    }
                });
    }

    private PendingResult.Callback<DirectionsResult> getBusRouteCallback(final List<BusStop> interStops, final BusStop startStop, final BusStop endStop
            , final LatLng startLatLng, final LatLng destLatLng) {
        return new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearMap();
                        DirectionsRoute[] routes = result.routes;
                        EncodedPolyline polyline = routes[0].overviewPolyline;
                        PolylineOptions options = new PolylineOptions();
                        for (com.google.maps.model.LatLng latLng : polyline.decodePath()) {
                            options.add(new LatLng(latLng.lat, latLng.lng));
                        }
                        options.color(getResources().getColor(R.color.color_primary)).width(15);
                        googleMap.addPolyline(options);
                        addGoogleWarning(result);
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for (BusStop bs : interStops) {
                            drawBusStop(bs);
                            boundsBuilder.include(bs.getLatLng());
                        }
                        googleMap.addMarker(new MarkerOptions()
                                .position(startStop.getLatLng())
                                .title(startStop.getName()));
                        googleMap.addMarker(new MarkerOptions()
                                .position(endStop.getLatLng())
                                .title(endStop.getName()));
                        boundsBuilder.include(startStop.getLatLng());
                        boundsBuilder.include(endStop.getLatLng());
                        boundsBuilder.include(startLatLng);
                        boundsBuilder.include(destLatLng);
                        changeZoomLevel(googleMap, boundsBuilder.build());
                        addWalkingPath(startLatLng, startStop.getLatLng(), true, false);
                        addWalkingPath(destLatLng, endStop.getLatLng(), false, false);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                activity.showErrorToast(R.string.no_bus_route);
                addWalkingPath(startLatLng, destLatLng, true, true);
            }
        };
    }

    private void addGoogleWarning(DirectionsResult result) {
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
        searchLoc.setText(builder.toString());
    }

    private DirectionsApiRequest getGoogleBusRoute(HashMap<String, TreeSet<BusStop>> map, String s, BusStop start, BusStop end, LinkedList<BusStop> stops) {
        DirectionsApiRequest req;
        LinkedList<String> waypoints = new LinkedList<>();
        int startID = start.routesMap.get(s), endID = end.routesMap.get(s);
        if (startID < endID) {
            //start before end, so just return stuff in the middle
            for (BusStop bs : map.get(s)) {
                if (bs.routesMap.get(s) > startID && endID > bs.routesMap.get(s)) {
                    waypoints.add(bs.getLatLng().latitude + "," + bs.getLatLng().longitude);
                    stops.add(bs);
                }
            }
        } else {
            //start bigger, so wrap around
            for (BusStop bs : map.get(s)) {
                if (bs.routesMap.get(s) > startID || endID > bs.routesMap.get(s)) {
                    waypoints.add(bs.getLatLng().latitude + "," + bs.getLatLng().longitude);
                    stops.add(bs);
                }
            }
        }
        req = DirectionsApi.getDirections(geoapi, start.getLatLng().latitude + "," + start.getLatLng().longitude,
                end.getLatLng().latitude + "," + end.getLatLng().longitude);
        if (!waypoints.isEmpty()) {
            req.waypoints(waypoints.toArray(new String[waypoints.size()]));
        }
        return req;
    }

    private void generateBusRouteMap(BusStop bs) {
        if (bs.routesMap == null) {
            bs.routesMap = new HashMap<>();
            Map<String, Double> bufferMap = (Map<String, Double>) bs.routes;
            if (bufferMap != null) {
                for (Map.Entry<String, Double> e : bufferMap.entrySet()) {
                    bs.routesMap.put(e.getKey(), ((int) (double) e.getValue()));
                }
            }
        }
    }

    private class BusStopComparator implements Comparator<BusStop>, Serializable {
        private String name;

        public BusStopComparator(String name) {
            this.name = name;
        }

        public int compare(BusStop b1, BusStop b2) {
            if (b1.routesMap == null) {
                generateBusRouteMap(b1);
            }
            if (b2.routesMap == null) {
                generateBusRouteMap(b1);
            }
            if (!b1.routesMap.containsKey(name) || !b2.routesMap.containsKey(name)) {
                return (int) (b1.getLatLng().latitude - b2.getLatLng().latitude);
            }
            return b1.routesMap.get(name) - b2.routesMap.get(name);
        }
    }

    public static void toggleRouteSelection(BusRoute busRoute) {
        if (selectedRoutes.contains(busRoute)) {
            selectedRoutes.remove(busRoute);
        } else {
            selectedRoutes.add(busRoute);
        }
    }

    private void loadRouteAdapter() {
        mLabs.routes().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<List<BusRoute>>() {
                    @Override
                    public void call(List<BusRoute> routes) {
                        routesAdapter = new RoutesAdapter(activity.getApplicationContext(), routes);
                        showRouteDialogBox();
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        activity.showErrorToast(R.string.no_path_found);
                    }
                });
    }

    private void showRouteDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.routes_list).setAdapter(routesAdapter, null)
                .setPositiveButton(R.string.routes_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        clearMap();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        if (selectedRoutes.isEmpty()) {
                            googleMap.setInfoWindowAdapter(buildingAdpater);
                        } else {
                            googleMap.setInfoWindowAdapter(transitAdapter);
                        }
                        for (BusRoute busRoute : selectedRoutes) {
                            drawOfficialRoute(builder, busRoute);
                        }
                        if (!selectedRoutes.isEmpty()) {
                            MapFragment.changeZoomLevel(googleMap, builder.build());
                        }
                    }
                }).show();
    }

    private void drawOfficialRoute(LatLngBounds.Builder builder, BusRoute busRoute) {
        PolylineOptions options = busRoute.getPolylineOptions();
        for (BusStop bs : busRoute.stops) {
            drawBusStop(bs);
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
    }

    private void drawBusStop(BusStop busStop) {
        busStopCircles.add(googleMap.addCircle(new CircleOptions()
                .center(busStop.getLatLng())
                .visible(true)
                .radius(CIRCLE_SIZE)
                .fillColor(getResources().getColor(R.color.white))
                .strokeWidth(3)
                .strokeColor(getResources().getColor(R.color.secondary_text))
                .zIndex(1)));
        busStopAdded.add(busStop);
    }

    private void checkBusStopClicked(LatLng pressed) {
        BusStop current = null;
        for (BusStop bs : busStopAdded) {
            float[] results = new float[1];
            Location.distanceBetween(pressed.latitude, pressed.longitude, bs.getLatLng().latitude, bs.getLatLng().longitude, results);
            if (results[0] <= SENSITIVITY_MULTIPLIER * CIRCLE_SIZE) {
                current = bs;
                busStopName.setText(current.getName());
                searchName.setText(current.getName());
                searchLoc.setText("");
                break;
            }
        }
        if (current != null) {
            for (Circle c : busStopCircles) {
                if (c.getCenter().equals(current.getLatLng())) {
                    if (c.equals(currentBusStop)) {
                        c.setFillColor(getResources().getColor(R.color.white));
                        currentBusStop = null;
                        searchName.setText(currentBuilding == null ? "" : currentBuilding.title);
                        searchLoc.setText(currentBuilding == null ? "" : currentBuilding.address);
                        busStopName.setText("");
                    } else {
                        c.setFillColor(getResources().getColor(R.color.red));
                        if (currentBusStop != null) {
                            currentBusStop.setFillColor(getResources().getColor(R.color.white));
                        }
                        currentBusStop = c;
                    }
                    return;
                }
            }
        }
    }

}
