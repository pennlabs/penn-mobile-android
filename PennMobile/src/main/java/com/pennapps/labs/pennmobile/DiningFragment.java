package com.pennapps.labs.pennmobile;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        mDiningHalls = new ArrayList<>();
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
        DiningHall diningHall = ((DiningAdapter.ViewHolder) v.getTag()).hall;
        if (diningHall.hasMenu()) {
            Fragment fragment = new MenuFragment();

            Bundle args = new Bundle();
            args.putParcelable("DiningHall", ((DiningAdapter.ViewHolder) v.getTag()).hall);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
                    JSONArray hours;
                    try {
                        hours = venue.getJSONArray("dateHours");
                    } catch (JSONException e) {
                        hours = new JSONArray();
                    }
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
                            MenuFragment.parseMeal(meal, mDiningHall);
                        }
                    }
                }
            } catch (JSONException ignored) {

            }
            return null;
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
