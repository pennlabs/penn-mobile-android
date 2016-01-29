package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.api.Labs;

import java.util.ArrayList;

/**
 * Created by Jason on 1/25/2016.
 */
public abstract class SearchFavoriteFragment extends ListFragment {

    protected Labs mLabs;
    protected MainActivity mActivity;
    protected ListView mListView;
    protected ViewPager viewPager;
    protected SearchView searchView;
    protected ListTabAdapter tabAdapter;

    protected abstract class ListTabAdapter extends FragmentStatePagerAdapter {

        public ListTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        /**
         * This function implements what the adapter will do given a query
         * @param query the query passed in from the search view
         */
        public abstract void onReceiveQuery (String query);

        protected void setIndex(int indexkey, int arraykey, String query) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int index = sharedPref.getInt(getString(indexkey), -1);
            String[] previouskey = getResources().getStringArray(arraykey);
            SharedPreferences.Editor editor = sharedPref.edit();
            if (index != -1) {
                boolean changed = false;
                for (int i = 4; i >= 0; i--) {
                    int id = (index + 5 - i) % 5;
                    String s = sharedPref.getString(previouskey[id], "");
                    if (s.equals(query)) {
                        changed = true;
                    }
                    if (changed && i > 0) {
                        int prev = (index + 5 - i + 1) % 5;
                        editor.putString(previouskey[id], sharedPref.getString(previouskey[prev], ""));
                    }
                }
                if (changed) {
                    editor.putString(previouskey[index], query);
                    editor.apply();
                    return;
                }
            }
            index = (index + 1) % 5;
            editor.putInt(getString(indexkey), index);
            editor.putString(previouskey[index], query);
            editor.apply();
            mActivity.closeKeyboard();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mLabs = MainActivity.getLabsInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.search_viewpager);
        tabAdapter = getAdapter();
        viewPager.setAdapter(tabAdapter);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String s = mListView.getAdapter().getItem(position).toString();
        tabAdapter.onReceiveQuery(s);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.closeKeyboard();
        mListView = getListView();
        setHasOptionsMenu(true);
    }

    /**
     * This method is to prepare an adapter for the view pager.
     * @return adapter  the view pager to be set onto the view pager
     */
    protected abstract ListTabAdapter getAdapter();

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.registrar, menu);
        searchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        searchView.setIconified(false);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                tabAdapter.onReceiveQuery(arg0);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
        final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange (View v, boolean hasFocus) {
                if (hasFocus) {
                    searchSuggestion(true);
                }
            }
        };

        final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchSuggestion(false);
                return false;
            }
        };
        searchView.setOnQueryTextFocusChangeListener(focusListener);
        searchView.setOnCloseListener(closeListener);
    }

    protected void searchSuggestion(boolean show) {
        if (show) {
            final ArrayList<String> list = new ArrayList<>(5);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
            int index = searchCount();
            if (index != -1) {
                String[] previouskey = getResources().getStringArray(previousArrayKey());
                for (int i = 0; i < 5; i++){
                    int id = (index + 5 - i) % 5;
                    String previous = sharedPref.getString(previouskey[id], "");
                    if (!previous.isEmpty()) {
                        list.add(previous);
                    }
                }
            }
            if (!list.isEmpty()) {
                mActivity.runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        mListView.setAdapter(new ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, list));
                    }
                }));
            }
        } else {
            viewPager.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the R.id for getStringArray call
     * @return id
     */
    protected abstract int previousArrayKey();

    /**
     * Returns the index of saved entries.
     * @return index   index of the most recent search entry, -1 if there is none.
     */
    protected abstract int searchCount();
}
