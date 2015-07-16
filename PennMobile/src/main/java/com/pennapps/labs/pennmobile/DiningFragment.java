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
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.ArrayList;
import java.util.List;

public class DiningFragment extends ListFragment {

    private DiningAPI mAPI;
    private Labs mLabs;
    private ListView mListView;
    private ArrayList<DiningHall> mDiningHalls;
    private Activity mActivity;
    public static Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = new DiningAPI(((MainActivity) getActivity()).getAPIClient());
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
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
            List<Venue> venues = mLabs.venues();
            for (Venue venue : venues) {
                DiningHall hall = new DiningHall(venue.id, venue.name, venue.isResidential(), venue.hasMenu(mLabs), venue.getHours());
                mDiningHalls.add(hall);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            new GetMenusTask().execute();
        }
    }

    private class GetMenusTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (DiningHall mDiningHall : mDiningHalls) {
                if (mDiningHall.isResidential() && mDiningHall.hasMenu()) {
                    NewDiningHall hall = mLabs.daily_menu(mDiningHall.getId());
                    mDiningHall.parseMeals(hall);
                }
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
    }
}
