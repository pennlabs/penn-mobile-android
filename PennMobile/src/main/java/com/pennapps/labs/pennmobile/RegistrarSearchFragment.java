package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.List;


public class RegistrarSearchFragment extends Fragment {

    public static final String COURSE_ID_EXTRA = "COURSE_ID";
    private Labs mLabs;
    public static Fragment mFragment;
    private Activity mActivity;
    private RegistrarAdapter mAdapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_registrar_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.registrar_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);
        searchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.registrar, menu);
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String input) {
                getActivity().findViewById(R.id.registrar_instructions).setVisibility(View.GONE);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                new GetRequestTask(input).execute();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {

        private String input;
        private List<Course> courses;

        GetRequestTask(String s) {
            input = s;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                courses = mLabs.courses(input);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (courses.size() == 0) {
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.registrar_fragment).setVisibility(View.GONE);
            } else {
                getActivity().findViewById(R.id.registrar_fragment).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.no_results).setVisibility(View.GONE);
                RegistrarListFragment listFragment = new RegistrarListFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.registrar_fragment, listFragment, "LIST")
                        .addToBackStack(null)
                        .commit();
                mAdapter = new RegistrarAdapter(mActivity.getApplicationContext(),
                        R.layout.search_entry, courses);
                listFragment.setListAdapter(mAdapter);
            }
        }
    }

}


