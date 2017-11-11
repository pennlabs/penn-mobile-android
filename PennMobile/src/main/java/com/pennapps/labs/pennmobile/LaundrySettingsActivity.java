package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryBuildingAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class LaundrySettingsActivity extends AppCompatActivity {

    private Labs mLabs;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.loadingPanel)
    RelativeLayout loadingPanel;
    @Bind(R.id.no_results)
    TextView no_results;
    @Bind(R.id.laundry_building_expandable_list)
    ExpandableListView mExpandableListView;
    @Bind(R.id.laundry_settings_help_layout)
    RelativeLayout mHelpLayout;

    private SharedPreferences sp;
    private Button mButton;

    private LaundryBuildingAdapter mAdapter;
    private int numRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_settings);

        ButterKnife.bind(this);

        mLabs = MainActivity.getLabsInstance();
        mContext = this;

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.laundry_settings_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHalls();
            }
        });

        // set up shared preferences
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        // reset laundry rooms button
        mButton = (Button) findViewById(R.id.laundry_room_reset);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // remove shared preferences
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("numRoomsSelected", 0);
                editor.apply();

                for (int i = 0; i < numRooms; i++) {
                    editor.remove(Integer.toString(i)).apply();
                }

                mExpandableListView.setAdapter(mAdapter);
            }
        });

        // set up back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getHalls();
    }

    private void getHalls() {
        mLabs.laundryRooms()
                .subscribe(new Action1<List<LaundryRoomSimple>>() {
                    @Override
                    public void call(final List<LaundryRoomSimple> rooms) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {

                                    numRooms = rooms.size();

                                    HashMap<String, List<LaundryRoomSimple>> hashMap = new HashMap<>();
                                    List<String> hallList = new ArrayList<>();

                                    int i = 0;
                                    // go through all the rooms
                                    while (i < rooms.size()) {

                                        // new list for the rooms in the hall
                                        List<LaundryRoomSimple> roomList = new ArrayList<>();

                                        // if hall name already exists, get the list of rooms and add to that
                                        String hallName = rooms.get(i).location;

                                        if (hallList.contains(hallName)) {
                                            roomList = hashMap.get(hallName);
                                            hashMap.remove(hallName);
                                            hallList.remove(hallName);
                                        }

                                        while (hallName.equals(rooms.get(i).location)) {
                                            roomList.add(rooms.get(i));

                                            i += 1;
                                            if (i >= rooms.size()) {
                                                break;
                                            }
                                        }

                                        // add the hall name to the list
                                        hallList.add(hallName);
                                        hashMap.put(hallName, roomList);
                                    }

                                    mAdapter = new LaundryBuildingAdapter(mContext, hashMap, hallList);
                                    mExpandableListView.setAdapter(mAdapter);

                                    loadingPanel.setVisibility(View.GONE);
                                    no_results.setVisibility(View.GONE);
                                }
                                try {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                                if (mHelpLayout != null) {
                                    mHelpLayout.setVisibility(View.GONE);
                                }
                                try {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }

    // back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, com.pennapps.labs.pennmobile.LaundryActivity.class);
        intent.putExtra("numRooms", numRooms);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}