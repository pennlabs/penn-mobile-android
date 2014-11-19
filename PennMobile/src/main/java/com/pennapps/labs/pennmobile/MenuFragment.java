package com.pennapps.labs.pennmobile;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pennapps.labs.pennmobile.api.DiningAPI;
import com.pennapps.labs.pennmobile.api.RegistrarAPI;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.pcr.RegCourse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {

    private DiningAPI mAPI;
    private TextView diningHallNameTV;
    private DiningHall mDiningHall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = new DiningAPI();
        mDiningHall = getArguments().getParcelable("DiningHall");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        diningHallNameTV = (TextView) v.findViewById(R.id.dining_hall_name);
        diningHallNameTV.setText(mDiningHall.getName());
        diningHallNameTV.setGravity(Gravity.CENTER_HORIZONTAL);
        fillDescriptions(v);
        try {
            if (mDiningHall.isResidential() && mDiningHall.hasMenu()) {
                JSONObject resultObj = mAPI.getDailyMenu(mDiningHall.getId());

                JSONArray meals = resultObj.getJSONObject("Document")
                        .getJSONObject("tblMenu")
                        .getJSONArray("tblDayPart");

                for (int i = 0; i < meals.length(); i++) {
                    JSONObject meal = meals.getJSONObject(i);
                    parseMeal(meal, mDiningHall);
                }
            }
        } catch (JSONException e) {
        }
        return v;
    }

    private void parseMeal(JSONObject meal, DiningHall diningHall) {
        try {
            String mealName = meal.getString("txtDayPartDescription");

            JSONArray stations = new JSONArray();
            try {
                stations = meal.getJSONArray("tblStation");
            } catch (JSONException e) {
                JSONObject stationsObject = meal.getJSONObject("tblStation");
                stations.put(stationsObject);
            }
            HashMap<String, String> currentMenu = new HashMap<String, String>();
            for (int j = 0; j < stations.length(); j++) {
                JSONObject station = stations.getJSONObject(j);
                parseStation(station, currentMenu);
            }

            if (mealName != null) {
                diningHall.menus.put(mealName, currentMenu);
            }
        } catch (JSONException ignored) {

        }
    }

    private void parseStation(JSONObject station, HashMap<String, String> menu) {
        try {
            String stationName = station.getString("txtStationDescription");
            JSONArray stationItems = new JSONArray();
            try {
                stationItems = station.getJSONArray("tblItem");
            } catch (JSONException e) {
                JSONObject stationItem = station.getJSONObject("tblItem");
                stationItems.put(stationItem);
            }
            for (int k = 0; k < stationItems.length(); k++) {
                JSONObject foodItem = stationItems.getJSONObject(k);
                String foodName = foodItem.getString("txtTitle");
                foodName = StringEscapeUtils.unescapeHtml4(foodName);
                if (menu.containsKey(stationName)) {
                    menu.put(stationName, menu.get(stationName) + ", " + foodName);
                } else {
                    menu.put(stationName, foodName);
                }
            }
        } catch (JSONException ignored) {

        }
    }

    public void fillDescriptions(View view) {
        DiningHall diningHall = mDiningHall;
        TextView hallNameTV = (TextView) view.findViewById(R.id.dining_hall_name);
        TextView hallStatus = (TextView) view.findViewById(R.id.dining_hall_status);
        TextView breakfastMenuTV = (TextView) view.findViewById(R.id.dining_hall_breakfast);
        TextView brunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_brunch);
        TextView lunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_lunch);
        TextView dinnerMenuTV = (TextView) view.findViewById(R.id.dining_hall_dinner);

        hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));
        view.setTag(diningHall);
        if (diningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(view.getContext().getResources().getDrawable(R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(view.getContext().getResources().getDrawable(R.drawable.label_red));
        }

        for (Map.Entry<String, HashMap<String, String>> menu : diningHall.menus.entrySet()) {
            String mealName = StringUtils.capitalize(menu.getKey());
            String menuText = "";
            for (Map.Entry<String, String> menuItem : menu.getValue().entrySet()) {
                String key = StringUtils.capitalize(menuItem.getKey());
                String value = menuItem.getValue();
                menuText += key + ": " + value + "\n";
            }
            if (mealName.equals("Breakfast")) {
                view.findViewById(R.id.dining_hall_breakfast_title).setVisibility(View.VISIBLE);
                breakfastMenuTV.setVisibility(View.VISIBLE);
                breakfastMenuTV.setText(menuText);
            } else if (mealName.equals("Brunch")) {
                view.findViewById(R.id.dining_hall_brunch_title).setVisibility(View.VISIBLE);
                brunchMenuTV.setVisibility(View.VISIBLE);
                brunchMenuTV.setText(menuText);
            } else if (mealName.equals("Lunch")) {
                view.findViewById(R.id.dining_hall_lunch_title).setVisibility(View.VISIBLE);
                lunchMenuTV.setVisibility(View.VISIBLE);
                lunchMenuTV.setText(menuText);
            } else if (mealName.equals("Dinner")) {
                view.findViewById(R.id.dining_hall_dinner_title).setVisibility(View.VISIBLE);
                dinnerMenuTV.setVisibility(View.VISIBLE);
                dinnerMenuTV.setText(menuText);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
