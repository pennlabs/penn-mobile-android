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
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.HomeScreenCell;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MainFragment extends Fragment {

    @BindView(R.id.home_screen_recyclerview) RecyclerView mRecyclerView;
    Unbinder unbinder;
    private Context mContext;
    private List<HomeScreenItem> visibleCategories;
    private SharedPreferences sharedPref;
    private List<HomeScreenItem> mAllCategories;
    private HomeScreenAdapter homeScreenAdapter;
    private List<HomeScreenCell> mCells;

    private Labs mLabsHome;
    private Labs mLabs;

    // laundry
    private List<LaundryRoom> mLaundryRoomList;
    private List<LaundryRoom> mLaundryRoomResult;
    private int numLaundryRoomsPref;

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
        mLabsHome = MainActivity.getLabsInstanceHome();
        mLabs = MainActivity.getLabsInstance();
    }

    private void orderCards() {

        // home screen categories/pages
        // In order: Courses, Dining, GSR Booking, Laundry, Map, News
        mAllCategories.add(new HomeScreenItem("Courses", 0));
        mAllCategories.add(new HomeScreenItem("Dining", 1));
        mAllCategories.add(new HomeScreenItem("GSR Booking", 2));
        mAllCategories.add(new HomeScreenItem("Laundry", 3));
        mAllCategories.add(new HomeScreenItem("Directory", 4));
        mAllCategories.add(new HomeScreenItem("News", 5));
//        mAllCategories.add(new HomeScreenItem("Spring Fling", 6));
        mAllCategories.add(new HomeScreenItem("NSO", 6));

        // determine which categories are visible
        int numVisibleCategories = 0;
        for (int i = 0; i < mAllCategories.size(); i++) {
            HomeScreenItem category = mAllCategories.get(i);
            int position = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + "_" + category.getName(), -1);
            if (position >= 100) {
                visibleCategories.add(category);
                numVisibleCategories++;
            }
        }

        // sort the categories based on the shared preferences data
        Collections.sort(visibleCategories, new Comparator<HomeScreenItem>() {
            @Override
            public int compare(HomeScreenItem homeScreenItem, HomeScreenItem t1) {
                String itemPrefName = mContext.getString(R.string.home_screen_pref) + "_" + homeScreenItem.getName();
                String t1PrefName = mContext.getString(R.string.home_screen_pref) + "_" + t1.getName();
                return sharedPref.getInt(itemPrefName, -1) % 100 - (sharedPref.getInt(t1PrefName, -1) % 100);
            }
        });

        // default preferences if none chosen
        if (numVisibleCategories == 0) {

            DateTime today = new DateTime();

            DateTime nsoStart = new DateTime(2018, 8, 15, 0, 0, 0, 0);
            DateTime nsoEnd = new DateTime(2018, 10, 15, 0, 0, 0, 0);
            Interval nsoDates = new Interval(nsoStart, nsoEnd);

//            DateTime flingStart = new DateTime(2019, 4, 10, 0, 0, 0, 0);
//            DateTime flingEnd = new DateTime(2019, 4, 15, 0, 0, 0, 0);
//            Interval flingDates = new Interval(flingStart, flingEnd);

            // default: dining, laundry, GSR
            visibleCategories.add(mAllCategories.get(1));
            visibleCategories.add(mAllCategories.get(3));
            visibleCategories.add(mAllCategories.get(2));

            // check if today is part of NSO dates - if so, add NSO to home page
            if (nsoDates.contains(today)) {
                visibleCategories.add(mAllCategories.get(6));
            }

//            // check if today is part of Fling dates - if so, add Fling to home page
//            if (flingDates.contains(today)) {
//                visibleCategories.add(mAllCategories.get(6));
//            }
        }

        // update home screen
        //getHomeData();
        homeScreenAdapter = new HomeScreenAdapter(mContext, visibleCategories, mCells, mLaundryRoomList);
        mRecyclerView.setAdapter(homeScreenAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        // settings
        setHasOptionsMenu(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.main_title);
        ((MainActivity) getActivity()).setNav(R.id.nav_home);

        mCells = new ArrayList<>();
        visibleCategories = new ArrayList<>();
        mAllCategories = new ArrayList<>();
        mLaundryRoomList = new ArrayList<>();
        mLaundryRoomResult = new ArrayList<>();
        numLaundryRoomsPref = 0;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // get preferences
    private void getHomeData() {
        mLabsHome.getHomePage("test_android").subscribe(new Action1<List<HomeScreenCell>>() {
            @Override
            public void call(final List<HomeScreenCell> cells) {

                mCells = cells;
                mLaundryRoomList = new ArrayList<>();
                mLaundryRoomResult = new ArrayList<>();

                // get laundry prefs and add data to list
                for (HomeScreenCell cell : mCells) {
                    if (cell.getType().equals("laundry")) {
                        int roomId = cell.getInfo().getRoomId();
                        numLaundryRoomsPref++;
                        addRoom(roomId);
                    }
                }

                // wait for laundry rooms to update
                while (mLaundryRoomResult.size() != numLaundryRoomsPref) {
                }

                // sort laundry rooms by name
                Collections.sort(mLaundryRoomResult, new Comparator<LaundryRoom>() {
                    @Override
                    public int compare(LaundryRoom room1, LaundryRoom room2) {
                        return room2.getId() - room1.getId();
                    }
                });
                mLaundryRoomList = mLaundryRoomResult;
                // update UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update adapter
                        homeScreenAdapter = new HomeScreenAdapter(mContext, visibleCategories, mCells, mLaundryRoomList);
                        mRecyclerView.setAdapter(homeScreenAdapter);
                        homeScreenAdapter.notifyDataSetChanged();
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    public synchronized void addRoomToList(LaundryRoom room) {
        mLaundryRoomResult.add(room);
    }

    private void addRoom(final int i) {
        mLabs.room(i)
                .subscribe(new Action1<LaundryRoom>() {
                    @Override
                    public void call(final LaundryRoom room) {
                        addRoomToList(room);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }
}
