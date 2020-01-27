package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.ArrayRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.SearchSuggestionAdapter;
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
    protected String lastQuery;
    private static int pagePosition;

    protected final static int MAX_SUGGESTION_SIZE = 5;
    private final static int ALPHA = 73;

    public abstract class ListTabAdapter extends FragmentStatePagerAdapter {

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
         * @return bol changed or not, the return value hasn't been used yet.
         */
        public abstract boolean onReceiveQuery (String query);

        public void setIndex(int indexKey, int arrayKey, String query) {
            addSearchQuery(indexKey, arrayKey, query, getActivity(), false);
            mActivity.closeKeyboard();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.search_all);
            }
            return getString(R.string.search_favorite);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mLabs = MainActivity.getLabsInstance();
        lastQuery = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.pager);
        tabAdapter = getAdapter();
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (position == 1) {
                    ((SearchFavoriteTab) getAdapter().getItem(1)).initList();
                } else {
                    ((SearchFavoriteTab) getAdapter().getItem(0)).processQuery(lastQuery);
                }
                pagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mActivity.addTabs(getAdapter(), viewPager, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.search_fav_fab);
        fabInit(fab);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        lastQuery = mListView.getAdapter().getItem(position).toString();
        hideSuggestion();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        mListView.setVisibility(View.GONE);
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
        final ImageView cross = (ImageView) searchView.findViewById(R.id.search_close_btn);
        cross.setImageResource(R.drawable.ic_clear_white_24dp);
        cross.setImageAlpha(ALPHA);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                cross.setImageAlpha(255);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                lastQuery = arg0;
                hideSuggestion();
                tabAdapter.onReceiveQuery(arg0);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
        final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange (View v, boolean hasFocus) {
                if (hasFocus) {
                    showSuggestion();
                }
            }
        };

        final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                hideSuggestion();
                mActivity.setTitle(getTitle());
                return false;
            }
        };
        searchView.setOnQueryTextFocusChangeListener(focusListener);
        searchView.setOnCloseListener(closeListener);

        cross.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getActionMasked() == MotionEvent.ACTION_UP) {
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cross.setImageAlpha(ALPHA);
                        }
                    }, 100);
                }
                return false;
            }
        });
    }

    protected void showSuggestion() {
        mActivity.setTitle("");
        mActivity.removeTabs();
        final ArrayList<String> list = new ArrayList<>(5);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        int index = searchCount();
        if (index != -1) {
            String[] previousKey = getResources().getStringArray(previousArrayKey());
            for (int i = 0; i < MAX_SUGGESTION_SIZE; i++){
                int id = (index + MAX_SUGGESTION_SIZE - i) % MAX_SUGGESTION_SIZE;
                String previous = sharedPref.getString(previousKey[id], "");
                if (!previous.isEmpty()) {
                    list.add(previous);
                }
            }
        }
        if (!list.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(new SearchSuggestionAdapter(mActivity, list));
        }
    }

    protected void hideSuggestion() {
        mActivity.addTabs(getAdapter(), viewPager, false);
        viewPager.setVisibility(View.VISIBLE);
        tabAdapter.onReceiveQuery(lastQuery);
        mListView.setVisibility(View.GONE);
    }

    /**
     * Returns the R.id for getStringArray call
     * @return id
     */
    protected abstract @ArrayRes int previousArrayKey();

    /**
     * Returns the index of saved entries.
     * @return index   index of the most recent search entry, -1 if there is none.
     */
    protected abstract int searchCount();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.removeTabs();
    }

    @Override
    public void onDestroyView() {
        mActivity.removeTabs();
        super.onDestroyView();
    }

    public static void addSearchQuery(int indexKey, int arrayKey, String query, Activity activity, boolean caseSensitive) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        int index = sharedPref.getInt(activity.getString(indexKey), -1);
        String[] previousKey = activity.getResources().getStringArray(arrayKey);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (index != -1) {
            boolean changed = false;
            for (int i = MAX_SUGGESTION_SIZE - 1; i >= 0; i--) {
                int id = (index + MAX_SUGGESTION_SIZE - i) % MAX_SUGGESTION_SIZE;
                String s = sharedPref.getString(previousKey[id], "");
                if (s.equals(query) || (caseSensitive && s.equalsIgnoreCase(query))) {
                    changed = true;
                }
                if (changed && i > 0) {
                    int prev = (index + MAX_SUGGESTION_SIZE - i + 1) % MAX_SUGGESTION_SIZE;
                    editor.putString(previousKey[id], sharedPref.getString(previousKey[prev], ""));
                }
            }
            if (changed) {
                editor.putString(previousKey[index], query);
                editor.apply();
                return;
            }
        }
        index = (index + 1) % MAX_SUGGESTION_SIZE;
        editor.putInt(activity.getString(indexKey), index);
        editor.putString(previousKey[index], query);
        editor.apply();
    }
    public static int getPagePosition() {
        return pagePosition;
    }

    /**
     * Get the title of the page
     * @return the string to be set as title
     */
    protected abstract String getTitle();

    /**
     * Set up the floating action button if necessary.  Default visibility gone
     * @param fab the floating action button
     */
    protected void fabInit(FloatingActionButton fab) {
        //Default nothing (visibility gone)
    }
}
