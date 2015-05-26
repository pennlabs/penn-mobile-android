package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.api.DiningAPI;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MenuFragment extends Fragment {

    private DiningAPI mAPI;
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
        v.setBackgroundColor(Color.WHITE);
        TextView diningHallNameTV = (TextView) v.findViewById(R.id.dining_hall_name);
        diningHallNameTV.setText(mDiningHall.getName());
        diningHallNameTV.setGravity(Gravity.LEFT);
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
        } catch (JSONException ignored) {
        }
        return v;
    }

    public static void parseMeal(JSONObject meal, DiningHall diningHall) {
        try {
            String mealName = meal.getString("txtDayPartDescription");

            JSONArray stations = new JSONArray();
            try {
                stations = meal.getJSONArray("tblStation");
            } catch (JSONException e) {
                JSONObject stationsObject = meal.getJSONObject("tblStation");
                stations.put(stationsObject);
            }
            HashMap<String, HashSet<String>> currentMenu = new HashMap<>();
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

    public static void parseStation(JSONObject station, HashMap<String, HashSet<String>> menu) {
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
                    HashSet<String> items = menu.get(stationName);
                    items.add(foodName);
                    menu.put(stationName, items);
                } else {
                    HashSet<String> items = new HashSet<>();
                    items.add(foodName);
                    menu.put(stationName, items);
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
            hallStatus.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.label_red));
        }

        for (Map.Entry<String, HashMap<String, HashSet<String>>> menu : diningHall.menus.entrySet()) {
            String mealName = StringUtils.capitalize(menu.getKey());
            String menuText = "";
            for (Map.Entry<String, HashSet<String>> menuItem : menu.getValue().entrySet()) {
                String key = StringUtils.capitalize(menuItem.getKey());
                HashSet<String> items = menuItem.getValue();
                String tab = "&nbsp&nbsp&nbsp ";
                menuText += "<b>" + key + "</b> <br>";
                for (String item : items) {
                    menuText += tab + item + "<br>";
                }
            }
            Spanned menuHtml = Html.fromHtml(menuText);
            switch (mealName) {
                case "Breakfast":
                    view.findViewById(R.id.dining_hall_breakfast_title).setVisibility(View.VISIBLE);
                    breakfastMenuTV.setVisibility(View.VISIBLE);
                    breakfastMenuTV.setText(menuHtml);
                    break;
                case "Brunch":
                    view.findViewById(R.id.dining_hall_brunch_title).setVisibility(View.VISIBLE);
                    brunchMenuTV.setVisibility(View.VISIBLE);
                    brunchMenuTV.setText(menuHtml);
                    break;
                case "Lunch":
                    view.findViewById(R.id.dining_hall_lunch_title).setVisibility(View.VISIBLE);
                    lunchMenuTV.setVisibility(View.VISIBLE);
                    lunchMenuTV.setText(menuHtml);
                    break;
                case "Dinner":
                    view.findViewById(R.id.dining_hall_dinner_title).setVisibility(View.VISIBLE);
                    dinnerMenuTV.setVisibility(View.VISIBLE);
                    dinnerMenuTV.setText(menuHtml);
                    break;
            }
        }
    }

}
