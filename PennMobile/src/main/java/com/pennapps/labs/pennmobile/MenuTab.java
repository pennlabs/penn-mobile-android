package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.pennapps.labs.pennmobile.adapters.MenuAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuTab extends Fragment {

    @BindView(R.id.menu_parent) LinearLayout menuParent;

    String meal;
    HashMap<String,List<String>> stationInfo = new HashMap<String,List<String>>();  //{station name: foods}
    private Unbinder unbinder;
    private ArrayList<String> stations;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        name = args.getString(getString(R.string.menu_arg_name), "Dining Hall");
        meal = args.getString(getString(R.string.menu_arg_meal), "Meal");
        stations = args.getStringArrayList(getString(R.string.menu_arg_stations,"Dining Stations"));
        
        for (String station : stations) {
            stationInfo.put(station, args.getStringArrayList(station));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return super.onOptionsItemSelected(item);

    }@Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_tab, container, false);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.station_list);
        elv.setFooterDividersEnabled(true);
        elv.addFooterView(new View(elv.getContext()));
        elv.setAdapter(new MenuAdapter(getActivity(), stations, stationInfo));
        v.setBackgroundColor(Color.WHITE);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
