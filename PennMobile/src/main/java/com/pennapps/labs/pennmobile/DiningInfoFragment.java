package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.VenueInterval;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Lily on 11/13/2015.
 * Fragment for Dining information (hours, map)
 */
public class DiningInfoFragment extends Fragment {

    private DiningHall mDiningHall;
    private MainActivity mActivity;
    private Labs mLabs;

    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Bind(R.id.dining_hours) RelativeLayout menuParent;
    @Bind(R.id.dining_map_frame) View mapFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
        mActivity = (MainActivity) getActivity();
        mLabs = MainActivity.getLabsInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining_info, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        fillInfo();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        FragmentManager fm = getChildFragmentManager();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().add(R.id.dining_map_container, mapFragment).commit();
            fm.executePendingTransactions();
        }
    }


    private void drawMap() {
        String buildingCode = mDiningHall.getName();
        if (!buildingCode.equals("")) {
            mLabs.buildings(buildingCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Building>>() {
                        @Override
                        public void call(List<Building> buildings) {
                            if (!buildings.isEmpty()) {
                                drawMarker(buildings.get(0).getLatLng());
                            }
                        }
                    });
        }
    }

    private void drawMarker(LatLng diningHallLatLng) {
        if (map != null && diningHallLatLng != null && mapFrame != null) {
            mapFrame.setVisibility(View.VISIBLE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(diningHallLatLng, 17));
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(diningHallLatLng)
                    .title(mDiningHall.getName()));
            marker.showInfoWindow();
        }
    }
    public void fillInfo(){
        if (mDiningHall.getVenue() != null) {
            List<VenueInterval> days = mDiningHall.getVenue().allHours();
            LinkedList<TextView> vertical = new LinkedList<>();
            for (VenueInterval day: days){
                vertical = addDiningHour(day, vertical);
            }
        }
    }

    public LinkedList<TextView> addDiningHour(VenueInterval day, LinkedList<TextView> vertical){
        TextView textView = new TextView(mActivity);
        DateTimeFormatter intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = intervalFormatter.parseDateTime(day.date);
        String dateString = dateTime.dayOfWeek().getAsText() + ", " + dateTime.monthOfYear().getAsString() + "/" + dateTime.dayOfMonth().getAsShortText();
        textView.setText(dateString);
        textView.setTextAppearance(mActivity, R.style.DiningInfoDate);
        textView.setPadding(0, 40, 0, 0);
        if (vertical.isEmpty()){
            textView.setId(0);
            textView.setId(textView.getId()+10);
            menuParent.addView(textView);
        } else {
            textView.setId(vertical.getLast().getId()+1);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            param.addRule(RelativeLayout.BELOW, vertical.getLast().getId());
            param.setMargins(0, 10, 10, 0);
            menuParent.addView(textView, param);
        }
        vertical.add(textView);
        for (VenueInterval.MealInterval meal: day.meals){
            TextView mealType = new TextView(mActivity);
            mealType.setText(meal.type);
            mealType.setId(vertical.getLast().getId() + 1);
            RelativeLayout.LayoutParams layparammeal = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layparammeal.addRule(RelativeLayout.BELOW, vertical.getLast().getId());
            layparammeal.setMargins(0, 10, 10, 0);
            menuParent.addView(mealType, layparammeal);
            vertical.add(mealType);

            RelativeLayout.LayoutParams layparamtimes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layparamtimes.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, vertical.getLast().getId());
            layparamtimes.addRule(RelativeLayout.ALIGN_BOTTOM, vertical.getLast().getId());
            layparamtimes.setMargins(0, 10, 0, 0);
            TextView mealInt = new TextView(mActivity);
            String hoursString = meal.getFormattedHour(meal.open) + " - " + meal.getFormattedHour(meal.close);
            mealInt.setText(hoursString);
            mealInt.setId(vertical.getLast().getId() + 1);
            menuParent.addView(mealInt, layparamtimes);
            vertical.add(mealInt);
        }
        return vertical;
    }
    @Override
    public void onResume() {
        super.onResume();
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        getActivity().setTitle(mDiningHall.getName());
        if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.95198, -75.19368), 17));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        }
        drawMap();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(mDiningHall.getName());
        ButterKnife.unbind(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

