package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.database.Cursor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;


public class RegistrarSearchFragment extends Fragment {

    public static final String COURSE_ID_EXTRA = "COURSE_ID";
    private CourseDatabase courseDatabase;
    public static Fragment mFragment;
    private Activity mActivity;
    private RegistrarAdapter mAdapter;
    private TextView textView;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        courseDatabase = new CourseDatabase(this.getActivity().getApplicationContext());
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
            public boolean onQueryTextSubmit(String arg0) {
                getActivity().findViewById(R.id.registrar_instructions).setVisibility(View.GONE);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                Cursor cursor = courseDatabase.getWordMatches(arg0.replaceAll("\\s+",""), null);
                RegistrarListFragment listFragment = new RegistrarListFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.registrar_fragment, listFragment, "LIST")
                        .addToBackStack(null)
                        .commit();
                mAdapter = new RegistrarAdapter(mActivity.getApplicationContext(),
                        R.layout.search_entry, cursor, 0);
                listFragment.setListAdapter(mAdapter);
                textView.setText(arg0);
                searchView.setQuery(arg0, false);
                return true;
            }
        };
        try {
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            textView = (TextView) searchView.findViewById(id);
            textView.setTextColor(Color.WHITE);
            searchView.setOnQueryTextListener(queryListener);
        } catch (NullPointerException e ) {

        }
    }

}


