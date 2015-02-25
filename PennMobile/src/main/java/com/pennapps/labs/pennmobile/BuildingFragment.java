package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.BuildingAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Building;

import java.util.List;

public class BuildingFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private Context mContext;
    private String mName;
    public static final String NAME_INTENT_EXTRA = "";
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        mName = getArguments().getString(DirectorySearchFragment.NAME_INTENT_EXTRA);
        new GetRequestTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_building, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
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
        getActivity().findViewById(R.id.building_instructions).setVisibility(View.GONE);
        MenuItem searchMenuItem = menu.findItem(R.id.building_search);
        searchView = (SearchView) menu.findItem(R.id.building_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
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
                // TODO: error check for filled in fields
                Fragment fragment = new BuildingFragment();
                Bundle args = new Bundle();
                args.putString(NAME_INTENT_EXTRA, arg0);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private List<Building> buildings;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                buildings = mLabs.buildings(mName);
                return true;
            } catch(Exception ignored) {
                ignored.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                // TODO:
                return;
            }
            try {
                BuildingAdapter mAdapter = new BuildingAdapter(mContext, buildings);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if (buildings.size() == 0) {
                    getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                } else {
                    mListView.setAdapter(mAdapter);
                    getActivity().findViewById(R.id.no_results).setVisibility(View.GONE);
                    getActivity().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
                }
                searchView.clearFocus();
            } catch (NullPointerException ignored) {

            }
        }
    }
}
