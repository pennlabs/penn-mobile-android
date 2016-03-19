package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.pennapps.labs.pennmobile.adapters.MenuAdapter;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuTab extends Fragment {
    String meal;
    HashMap<String,List<String>> stationInfo = new HashMap<String,List<String>>();  //{station name: foods}
    private ArrayList<String> stations;
    private String name;
    @Bind(R.id.menu_parent)
    LinearLayout menuParent;

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
        elv.setAdapter(new MenuAdapter(getActivity(), stations, stationInfo));
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(name);
        ButterKnife.unbind(this);
    }


}
