package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.pennapps.labs.pennmobile.adapters.HomeScreenSettingsAdapter;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;
import com.pennapps.labs.pennmobile.classes.HomeScreenItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeScreenSettingsActivity extends AppCompatActivity {

    @BindView(R.id.home_screen_settings_recyclerview) RecyclerView mRecyclerView;

    private Context mContext;
    private List<HomeScreenItem> mAllCategories;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_settings);
        ButterKnife.bind(this);

        // set up back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.mContext = this;
        mAllCategories = new ArrayList<>();
        mAllCategories.add(new HomeScreenItem("Courses", 0));
        mAllCategories.add(new HomeScreenItem("Dining", 1));
        mAllCategories.add(new HomeScreenItem("GSR Booking", 2));
        mAllCategories.add(new HomeScreenItem("Laundry", 3));
        mAllCategories.add(new HomeScreenItem("Directory", 4));
        mAllCategories.add(new HomeScreenItem("News", 5));
        mAllCategories.add(new HomeScreenItem("NSO", 6));
        mAllCategories.add(new HomeScreenItem("Fitness", 7));
//        mAllCategories.add(new HomeScreenItem("Spring Fling", 6));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        // sort the categories using the shared preference data so SettingsAdapter will display
        // the categories in the right order
        Collections.sort(mAllCategories, new Comparator<HomeScreenItem>() {
            @Override
            public int compare(HomeScreenItem homeScreenItem, HomeScreenItem t1) {
                String itemPrefName = mContext.getString(R.string.home_screen_pref) + "_" + homeScreenItem.getName();
                String t1PrefName = mContext.getString(R.string.home_screen_pref) + "_" + t1.getName();
                return sharedPref.getInt(itemPrefName, 150) % 100 - (sharedPref.getInt(t1PrefName, 150) % 100);
            }
        });
        HomeScreenSettingsAdapter adapter = new HomeScreenSettingsAdapter(mContext, mAllCategories);
        mRecyclerView.setAdapter(adapter);

        // add horizontal divider for RecyclerView
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        // add handling of cardview drags to move position
        ItemTouchHelper.Callback callback = new HomeScreenItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
