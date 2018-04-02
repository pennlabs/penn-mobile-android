package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

public class HomeScreenSettingsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private List<HomeScreenItem> mAllCategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_settings);

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

        mRecyclerView = (RecyclerView) findViewById(R.id.home_screen_settings_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
