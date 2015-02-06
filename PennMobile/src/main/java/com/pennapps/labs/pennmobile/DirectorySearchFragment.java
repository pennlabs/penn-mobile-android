package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DirectorySearchFragment extends Fragment {

    public static final String NAME_INTENT_EXTRA = "";
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory, container, false);
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
            case R.id.directory_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.directory_search);
        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        searchView.setQuery("", false);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO: error check for filled in fields
                getActivity().findViewById(R.id.directory_instructions).setVisibility(View.GONE);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                Fragment fragment = new DirectoryFragment();
                Bundle args = new Bundle();
                args.putString(NAME_INTENT_EXTRA, arg0);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

}
