package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;

public class LaundryActivity extends AppCompatActivity {

    // views
    @BindView(R.id.laundry_help_text)
    TextView mTextView;
    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;
    @BindView(R.id.no_results)
    TextView no_results;
    @BindView(R.id.favorite_laundry_list)
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Labs mLabs;
    private Context mContext;

    private SharedPreferences sp;

    // list of favorite laundry rooms
    private ArrayList<LaundryRoom> laundryRooms = new ArrayList<>();
    // data for laundry room usage
    private List<LaundryUsage> roomsData = new ArrayList<>();

    private ArrayList<LaundryRoom> laundryRoomsResult = new ArrayList<>();
    private List<LaundryUsage> roomsDataResult = new ArrayList<>();

    private LaundryRoomAdapter mAdapter;

    private int count;
    private int numRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);

        // back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLabs = MainActivity.getLabsInstance();
        mContext = this;

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        numRooms = sp.getInt(mContext.getString(R.string.num_rooms_pref), 100);
      
        // get num rooms to display
        count = 0;
        for (int i = 0; i < numRooms; i++) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                count += 1;
            }
        }
        Fabric.with(this, new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Laundry")
                .putContentType("App Feature")
                .putContentId("3"));

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.laundry_machine_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRooms();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);

        // no rooms chosen
        if (count == 0) {
            loadingPanel.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mAdapter = new LaundryRoomAdapter(mContext, laundryRooms, roomsData, false);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.laundry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.laundry_settings) {
            Intent intent = new Intent(this, com.pennapps.labs.pennmobile.LaundrySettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        numRooms = sp.getInt(mContext.getString(R.string.num_rooms_pref), 100);

        // get num rooms to display
        count = 0;
        for (int i = 0; i < numRooms; i++) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                count += 1;
            }
        }

        loadingPanel.setVisibility(View.VISIBLE);
        updateRooms();
    }

    private void updateRooms() {

        laundryRooms = new ArrayList<>();
        roomsData = new ArrayList<>();
        roomsDataResult = new ArrayList<>();
        laundryRoomsResult = new ArrayList<>();

        // add data
        for (int i = 0; i < numRooms; i++) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                addAvailability(i);
                addRoom(i);
            }
        }

        // no rooms chosen
        if (count == 0) {
            loadingPanel.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mAdapter = new LaundryRoomAdapter(mContext, laundryRooms, roomsData, false);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private synchronized void addRoomToList(LaundryRoom room) {
        laundryRoomsResult.add(room);
    }

    private synchronized void addUsageToList(LaundryUsage usage) {
        roomsDataResult.add(usage);
    }

    private void addRoom(final int i) {
        mLabs.room(i)
                .subscribe(new Action1<LaundryRoom>() {
                    @Override
                    public void call(final LaundryRoom room) {
                        room.setId(i);
                        addRoomToList(room);

                        if (laundryRoomsResult.size() == count) {

                            // sort laundry rooms data by hall name
                            Collections.sort(roomsDataResult, new Comparator<LaundryUsage>() {
                                @Override
                                public int compare(LaundryUsage usage1, LaundryUsage usage2) {
                                    return usage2.getId() - usage1.getId();
                                }
                            });

                            // sort laundry rooms by name
                            Collections.sort(laundryRoomsResult, new Comparator<LaundryRoom>() {
                                @Override
                                public int compare(LaundryRoom room1, LaundryRoom room2) {
                                    return room2.getId() - room1.getId();
                                }
                            });

                            boolean loading = false;
                            // make sure results are finished loading
                            while (roomsDataResult.size() != count) {
                                loading = true;
                            }

                            // update UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadingPanel != null) {

                                        roomsData = roomsDataResult;
                                        laundryRooms = laundryRoomsResult;
                                        mAdapter = new LaundryRoomAdapter(mContext, laundryRooms, roomsData, false);
                                        mRecyclerView.setAdapter(mAdapter);

                                        loadingPanel.setVisibility(View.GONE);
                                        mTextView.setVisibility(View.INVISIBLE);
                                    }
                                    try {
                                        swipeRefreshLayout.setRefreshing(false);
                                    } catch (NullPointerException e) {
                                        //it has gone to another page.
                                    }
                                }
                            });
                        }
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
                                if (mTextView != null) {
                                    mTextView.setVisibility(View.GONE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }

    private void addAvailability(final int i) {
        mLabs.usage(i)
                .subscribe(new Action1<LaundryUsage>() {
                    @Override
                    public void call(final LaundryUsage usage) {
                        usage.setId(i);
                        addUsageToList(usage);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        // in case usage data not available - set chart to 0
                        LaundryUsage newUsage = new LaundryUsage();
                        newUsage.setWasherData();
                        newUsage.setDryerData();
                        newUsage.setId(i);
                        roomsDataResult.add(newUsage);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                                if (mTextView != null) {
                                    mTextView.setVisibility(View.GONE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }
}