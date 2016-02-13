package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

public class MenuFragment extends Fragment {

    private DiningHall mDiningHall;
    private MainActivity mActivity;
    @Bind(R.id.menu_parent) LinearLayout menuParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.dining_info_button:
                Fragment fragment = new DiningInfoFragment();
                Bundle args = new Bundle();
                args.putParcelable("DiningHall", getArguments().getParcelable("DiningHall"));
                fragment.setArguments(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.dining_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.station_list);
        List<String> headers = new ArrayList<String>();
        HashMap<String,List<String>> stationInfo = new HashMap<String,List<String>>();
        for (DiningHall.Menu menu: mDiningHall.menus) {
            stationInfo.putAll(getStations(menu));
            headers.addAll(stationInfo.keySet());
        }
        elv.setAdapter(new MenuAdapter(getActivity(), headers, stationInfo));
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        return v;
    }
    public HashMap<String,List<String>> getStations(DiningHall.Menu menu){
        HashMap<String, List<String>> stations = new HashMap<String, List<String>>();
        for (DiningHall.DiningStation station : menu.stations) {
            List<String> foods = new ArrayList<String>();
            StringBuilder food = new StringBuilder();
            for (DiningHall.FoodItem item: station.items){
                food.append(item.title);
                food.append("\n");
            }
            foods.add(food.toString());
            stations.put(StringUtils.capitalize(station.name), foods);
        }
        return stations;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        getActivity().setTitle(mDiningHall.getName() + " Menu");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.dining);
        ButterKnife.unbind(this);
    }
}