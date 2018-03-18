package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.adapters.HomeScreenAdapter;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private List<HomeScreenItem> mCategories;
    private SharedPreferences sharedPref;
    private List<HomeScreenItem> mAllCategories;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).closeKeyboard();
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private void orderCards() {
        // home screen categories/pages
        // In order: Courses, Dining, GSR Booking, Laundry, Map, News
        mAllCategories = new ArrayList<>();
        mAllCategories.add(new HomeScreenItem("Courses", 0));
        mAllCategories.add(new HomeScreenItem("Dining", 1));
        mAllCategories.add(new HomeScreenItem("GSR Booking", 2));
        mAllCategories.add(new HomeScreenItem("Laundry", 3));
        mAllCategories.add(new HomeScreenItem("Map", 4));
        mAllCategories.add(new HomeScreenItem("News", 5));
        mAllCategories.add(new HomeScreenItem("Spring Fling", 6));

        mCategories = new ArrayList<>();
        // determine order of cards
        for (int index = 0; index < mAllCategories.size(); index++) {
            // search all categories to find the one that belongs to correct index
            for (int j = 0; j < mAllCategories.size(); j++) {
                int position = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + j, -1);
                if (position == index) {
                    HomeScreenItem category = mAllCategories.get(j);
                    mCategories.add(index, category);
                    break;
                }
            }
        }

        HomeScreenAdapter adapter = new HomeScreenAdapter(mContext, mCategories);
        mRecyclerView.setAdapter(adapter);
    }

    private void reset() {
        for (int i = 0; i < mAllCategories.size(); i++) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(mContext.getString(R.string.home_screen_pref) + i, -1);
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_update, container, false);

        // settings
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_screen_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.main_title);
        ((MainActivity) getActivity()).setNav(R.id.nav_home);
        orderCards();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_screen_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_screen_settings) {
            Intent intent = new Intent(mContext, HomeScreenSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
