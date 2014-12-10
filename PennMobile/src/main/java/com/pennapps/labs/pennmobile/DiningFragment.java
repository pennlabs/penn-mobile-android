package com.pennapps.labs.pennmobile;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.DiningAdapter;
import com.pennapps.labs.pennmobile.api.DiningAPI;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DiningFragment extends ListFragment {

    private DiningAPI mAPI;
    private ListView mListView;
    private ArrayList<DiningHall> mDiningHalls;
    private Activity mActivity;
    public static Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = new DiningAPI();
        mActivity = getActivity();
        mDiningHalls = new ArrayList<DiningHall>();
        mFragment = this;
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        new GetOpenTask().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dining, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DiningHall diningHall = (DiningHall) v.getTag();
        if (diningHall.hasMenu()) {
            Fragment fragment = new MenuFragment();

            Bundle args = new Bundle();
            args.putParcelable("DiningHall", (Parcelable) v.getTag());
            fragment.setArguments(args);

            FragmentManager fragmentManager = this.getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.dining_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
            onResume();
        }
    }
    private class GetOpenTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                JSONObject resultObj = mAPI.getVenues();
                JSONArray venues = resultObj.getJSONObject("document").getJSONArray("venue");
                for (int i = 0; i < venues.length(); i++) {
                    JSONObject venue = venues.getJSONObject(i);
                    int id = venue.getInt("id");
                    String name = venue.getString("name");
                    boolean isResidential = venue.getString("venueType").equals("residential") && !name.equals("Cafe at McClelland");
                    boolean hasMenu = hasMenu(venue);
                    JSONArray hours = venue.getJSONArray("dateHours");
                    mDiningHalls.add(new DiningHall(id, name, isResidential, hasMenu, hours));
                }
            } catch (JSONException | NullPointerException ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            new GetMenusTask().execute();
        }
    }

    private boolean hasMenu(JSONObject venue) {
        try {
            if (venue.getString("dailyMenuURL").isEmpty()) {
                return false;
            } else {
                JSONObject meals = mAPI.getDailyMenu(venue.getInt("id")).getJSONObject("Document")
                        .getJSONObject("tblMenu");
                if (meals.length() == 0) {
                    return false;
                }
            }
        } catch (JSONException ignored) {
            return false;
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }

    private class GetMenusTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (DiningHall mDiningHall : mDiningHalls) {
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
                }
            } catch (JSONException ignored) {

            }
            return null;
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

        @Override
        protected void onPostExecute(Void params) {
            try {
                DiningAdapter mAdapter = new DiningAdapter(mActivity, mDiningHalls);
                mListView.setAdapter(mAdapter);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            } catch (NullPointerException ignored) {

            }
        }
    }
}
