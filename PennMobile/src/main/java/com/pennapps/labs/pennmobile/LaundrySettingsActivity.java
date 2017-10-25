package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.adapters.LaundryBuildingAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LaundrySettingsActivity extends AppCompatActivity {

    // TODO get json from website
    private String json = "[\n" +
            "  {\"id\": \"0\", \"name\": \"Bishop White\"}, \n" +
            "  {\"id\": \"1\", \"name\": \"Chestnut Butcher\"},\n" +
            "  {\"id\": \"2\", \"name\": \"Class of 1928 Fisher\"},\n" +
            "  {\"id\": \"3\", \"name\": \"Craig\"}, \n" +
            "  {\"id\": \"4\", \"name\": \"DuBois\"},\n" +
            "  {\"id\": \"5\", \"name\": \"English House\"},\n" +
            "  {\"id\": \"6\", \"name\": \"Harnwell_Floor 02\"}, \n" +
            "  {\"id\": \"7\", \"name\": \"Harnwell_Floor 04\"}, \n" +
            "  {\"id\": \"8\", \"name\": \"Harnwell_Floor 06\"}, \n" +
            "  {\"id\": \"9\", \"name\": \"Harnwell_Floor 08\"},\n" +
            "  {\"id\": \"10\", \"name\": \"Harnwell_Floor 10\"}, \n" +
            "  {\"id\": \"11\", \"name\": \"Harnwell_Floor 12\"}, \n" +
            "  {\"id\": \"12\", \"name\": \"Harnwell_Floor 14\"}, \n" +
            "  {\"id\": \"13\", \"name\": \"Harnwell_Floor 16\"}, \n" +
            "  {\"id\": \"14\", \"name\": \"Harnwell_Floor 18\"}, \n" +
            "  {\"id\": \"15\", \"name\": \"Harnwell_Floor 20\"}, \n" +
            "  {\"id\": \"16\", \"name\": \"Harnwell_Floor 22\"}, \n" +
            "  {\"id\": \"17\", \"name\": \"Harnwell_Floor 24\"}, \n" +
            "  {\"id\": \"18\", \"name\": \"Harrison_Floor 04\"}, \n" +
            "  {\"id\": \"19\", \"name\": \"Harrison_Floor 06\"},  \n" +
            "  {\"id\": \"20\", \"name\": \"Harrison_Floor 08\"}, \n" +
            "  {\"id\": \"21\", \"name\": \"Harrison_Floor 10\"}, \n" +
            "  {\"id\": \"22\", \"name\": \"Harrison_Floor 12\"}, \n" +
            "  {\"id\": \"23\", \"name\": \"Harrison_Floor 14\"}, \n" +
            "  {\"id\": \"24\", \"name\": \"Harrison_Floor 16\"}, \n" +
            "  {\"id\": \"25\", \"name\": \"Harrison_Floor 18\"}, \n" +
            "  {\"id\": \"26\", \"name\": \"Harrison_Floor 20\"}, \n" +
            "  {\"id\": \"27\", \"name\": \"Harrison_Floor 22\"}, \n" +
            "  {\"id\": \"28\", \"name\": \"Harrison_Floor 24\"}, \n" +
            "  {\"id\": \"29\", \"name\": \"Hill House\"}, \n" +
            "  {\"id\": \"30\", \"name\": \"Magee Amhurst\"}, \n" +
            "  {\"id\": \"31\", \"name\": \"Mayer\"}, \n" +
            "  {\"id\": \"32\", \"name\": \"Morgan\"}, \n" +
            "  {\"id\": \"33\", \"name\": \"Rodin_Floor 02\"}, \n" +
            "  {\"id\": \"34\", \"name\": \"Rodin_Floor 04\"}, \n" +
            "  {\"id\": \"35\", \"name\": \"Rodin_Floor 06\"}, \n" +
            "  {\"id\": \"36\", \"name\": \"Rodin_Floor 08\"}, \n" +
            "  {\"id\": \"37\", \"name\": \"Rodin_Floor 10\"}, \n" +
            "  {\"id\": \"38\", \"name\": \"Rodin_Floor 12\"}, \n" +
            "  {\"id\": \"39\", \"name\": \"Rodin_Floor 14\"},  \n" +
            "  {\"id\": \"40\", \"name\": \"Rodin_Floor 16\"}, \n" +
            "  {\"id\": \"41\", \"name\": \"Rodin_Floor 18\"}, \n" +
            "  {\"id\": \"42\", \"name\": \"Rodin_Floor 20\"}, \n" +
            "  {\"id\": \"43\", \"name\": \"Rodin_Floor 22\"}, \n" +
            "  {\"id\": \"44\", \"name\": \"Rodin_Floor 24\"}, \n" +
            "  {\"id\": \"45\", \"name\": \"Sansom East\"}, \n" +
            "  {\"id\": \"46\", \"name\": \"Sansom West\"}, \n" +
            "  {\"id\": \"47\", \"name\": \"Stouffer Commons\"}\n" +
            "]";

    private Labs mLabs;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.loadingPanel)
    RelativeLayout loadingPanel;
    @Bind(R.id.no_results)
    TextView no_results;
    @Bind(R.id.laundry_building_expandable_list)
    ExpandableListView mExpandableListView;

    private ArrayList<LaundryRoomSimple> rooms = new ArrayList<>();

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

        // set up back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        rooms = getLaundryRoomList();
        getHalls();
    }

    private void getHalls() {
        HashMap<String, List<LaundryRoomSimple>> hashMap = new HashMap<>();
        List<String> hallList = new ArrayList<>();

        int i = 0;
        // go through all the rooms
        while (i < rooms.size()) {

            String roomName = rooms.get(i).name;
            String hallName = roomName;

            // new list for the rooms in the hall
            List<LaundryRoomSimple> roomList = new ArrayList<>();

            // if there is more than one room
            if (roomName.contains("_")) {
                hallName = roomName.substring(0, roomName.indexOf("_"));

                while (hallName.equals(rooms.get(i).name.substring(0, rooms.get(i).name.indexOf("_")))) {
                    roomList.add(rooms.get(i));
                    i += 1;
                    if (!rooms.get(i).name.contains("_")) {
                        break;
                    }
                }
            }
            // if there is only one room
            else {
                roomList.add(rooms.get(i));
                i += 1;
            }

            // add the hall name to the list
            hallList.add(hallName);
            hashMap.put(hallName, roomList);
        }

        LaundryBuildingAdapter adapter = new LaundryBuildingAdapter(mContext, hashMap, hallList);
        mExpandableListView.setAdapter(adapter);

        loadingPanel.setVisibility(View.GONE);
        no_results.setVisibility(View.GONE);

        try {
            mSwipeRefreshLayout.setRefreshing(false);
        } catch (NullPointerException e) {
            //it has gone to another page.
        }
    }

    private ArrayList<LaundryRoomSimple> getLaundryRoomList() {
        return new Gson().fromJson(json, new TypeToken<ArrayList<LaundryRoomSimple>>() {
        }.getType());
    }

    // back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, com.pennapps.labs.pennmobile.LaundryActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}