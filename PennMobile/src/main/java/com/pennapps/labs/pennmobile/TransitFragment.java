package com.pennapps.labs.pennmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusPath;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private RoutesAdapter adapter;
    private boolean[] routesClicked;

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
    public void onStart(){
        super.onStart();
        mapCallBacks.getGoogleApiClient().connect();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        getActivity().setTitle(R.string.transit);
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
                if(adapter == null){
                    mLabs.routes().observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<BusRoute>>() {
                        @Override
                        public List<BusRoute> call(Throwable throwable) {
                            return null;
                        }
                    }).subscribe(new Action1<List<BusRoute>>(){
                        @Override
                        public void call(List<BusRoute> routes) {
                            ArrayList<String> route_names = new ArrayList<>(routes.size());
                            for(BusRoute route: routes){
                                route_names.add(route.route_name);
                            }
                            adapter = new RoutesAdapter(getActivity().getApplicationContext(),
                                    routes, route_names);
                            showRouteDialogBox();

                        }
                    });
                } else{
                    showRouteDialogBox();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRouteDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.routes_list).setAdapter(adapter, null)
        .setPositiveButton(R.string.routes_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                googleMap.clear();
                for(int i = 0; i < adapter.getCount(); i++){
                    adapter.drawRoutes(i);
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
                } else {
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
                if (beginL == null) {
                    drawMap(dest, begin, latLng);
                } else {
                    final LatLng endL = latLng;
                    if (endL == null) {
                        return;
                    }
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
                                    PolylineOptions options = new PolylineOptions();
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (BusStop busStop : route.path) {
                                        LatLng latLngBuff = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        if (busStop.getName() != null) {
                                            if (route.path.indexOf(busStop) != 0
                                                    && route.path.indexOf(busStop) != route.path.size() - 1) {
                                                googleMap.addMarker(new MarkerOptions()
                                                        .position(latLngBuff)
                                                        .title(busStop.getName())
                                                        .icon(BitmapDescriptorFactory
                                                                .fromResource(R.drawable.ic_brightness_1_black_18dp)));
                                            } else {
                                                googleMap.addMarker(new MarkerOptions()
                                                        .position(latLngBuff)
                                                        .title(busStop.getName()));
                                            }
                                        }
                                        options.add(latLngBuff);
                                        builder.include(latLngBuff);
                                    }
                                    builder.include(beginL);
                                    builder.include(endL);
                                    options.width(15).color(Color.BLUE);
                                    changeZoomLevel(builder.build());
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

    private class RoutesAdapter extends ArrayAdapter<String> {
        int[] colors = {Color.rgb(76, 175, 80), Color.rgb(244, 67, 54), Color.rgb(63, 81, 181), Color.BLACK, Color.GRAY};
        ArrayList<String> v;
        Context c;
        List<BusRoute> routes;
        Polyline[] polylines;
        HashMap<Polyline, HashSet<Marker>> markers;
        RoutesAdapter(Context context, List<BusRoute> routes, ArrayList<String> values){
            super(context, R.layout.route_list_item, values);
            v = values;
            c = context;
            this.routes = routes;
            polylines = new Polyline[routes.size()];
            markers = new HashMap<>();
            routesClicked = new boolean[values.size()];
            for(boolean b: routesClicked){
                b = false;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            View rowView = convertView;
            if(rowView == null)
            {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.route_list_item, parent, false);
            }
            ((TextView)rowView.findViewById(R.id.routes_name)).setText(v.get(position));
            final Button b = (Button) rowView.findViewById(R.id.routes_checkbox);
            updateRoutes(index, b);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    routesClicked[index] = !routesClicked[index];
                    updateRoutes(index, v);
                }
            });
            b.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    b.setLayoutParams(new LinearLayout.LayoutParams(b.getMeasuredHeight(), b.getMeasuredHeight()));
                }
            });
            return rowView;
        }

        public void drawRoutes(int index){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if(routesClicked[index]){
                PolylineOptions options = new PolylineOptions();
                HashSet<Marker> markerSet = new HashSet<>();
                for (BusStop busStop : routes.get(index).stops) {
                    for(BusStop bs: busStop.path_to){
                        options.add(new LatLng(bs.getLatitude(), bs.getLongitude()));
                        builder.include(new LatLng(bs.getLatitude(), bs.getLongitude()));
                    }
                    LatLng latLngBuff = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                    if (busStop.getName() != null) {
                        markerSet.add(googleMap.addMarker(new MarkerOptions()
                                .position(latLngBuff)
                                .title(busStop.getName())
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_brightness_1_black_18dp))));

                    }
                    options.add(latLngBuff);
                    builder.include(latLngBuff);
                }
                for(BusStop bs: routes.get(index).stops.get(0).path_to){
                    options.add(new LatLng(bs.getLatitude(), bs.getLongitude()));
                    builder.include(new LatLng(bs.getLatitude(), bs.getLongitude()));
                }
                LatLng latLngBuff = new LatLng(routes.get(index).stops.get(0).getLatitude(),
                        routes.get(index).stops.get(0).getLongitude());
                options.add(latLngBuff);
                builder.include(latLngBuff);
                options.width(15).color(colors[index]);
                polylines[index] = googleMap.addPolyline(options);
                markers.put(polylines[index], markerSet);
                changeZoomLevel(builder.build());
            } else{
                if(polylines[index] != null){
                    polylines[index].remove();
                }
                if(markers.containsKey(polylines[index])){
                    for(Marker m: markers.get(polylines[index])){
                        m.remove();
                    }
                    markers.remove(polylines[index]);
                }
            }
        }
        private void updateRoutes(int index, View v){
            if(routesClicked[index]){
                Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(colors[index]);
                RectF rectf = new RectF(15, 15, 60, 60);
                canvas.drawRoundRect(rectf,10, 10, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRoundRect(rectf, 10, 10, paint);
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                if(v != null) {
                    v.setBackground(drawable);
                }
            } else{
                Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                RectF rectf = new RectF(15, 15, 60, 60);
                canvas.drawRoundRect(rectf, 10, 10, paint);
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                if(v != null) {
                    v.setBackground(drawable);
                }
            }
        }
    }

    private void changeZoomLevel(LatLngBounds bounds){
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
}