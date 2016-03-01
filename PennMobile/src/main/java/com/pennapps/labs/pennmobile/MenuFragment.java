package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ViewFlipper;
import android.widget.ExpandableListView;

import com.pennapps.labs.pennmobile.adapters.MenuAdapter;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment {

    TabAdapter pageAdapter;
    ViewPager pager;

    private DiningHall mDiningHall;
    private MainActivity mActivity;

    class TabAdapter extends FragmentStatePagerAdapter {

        ArrayList<HashMap<String, ArrayList<String>>> foods;  //for each meal: {name of station: arraylist of foods at the station}
        ArrayList<String> meals;
        String name;

        public TabAdapter(FragmentManager fm) {
            super(fm);
            foods = new ArrayList<HashMap<String, ArrayList<String>>>();
            meals = new ArrayList<>();
        }

        private void addTabs(DiningHall hall) {
            List<DiningHall.Menu> menus = hall.menus;
            name = hall.getName();
            for (DiningHall.Menu menu: menus) {
                HashMap<String, ArrayList<String>> stations = new HashMap<String, ArrayList<String>>();
                meals.add(menu.name);
                for (DiningHall.DiningStation station : menu.stations) {
                    ArrayList<String> foods = new ArrayList<>();
                    for (DiningHall.FoodItem item: station.items){
                        foods.add(item.title);
                    }
                    stations.put(StringUtils.capitalize(station.name), foods);
                }
                foods.add(stations);
            }
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = new MenuTab();
            Bundle args = new Bundle();
            args.putString("name",name);
            args.putStringArrayList("stations", new ArrayList<String>(foods.get(position).keySet()));
            HashMap<String, ArrayList<String>> stations = foods.get(position);
            for (String station: stations.keySet()){
                args.putStringArrayList(station, stations.get(station));
            }
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return meals.get(position);
        }

        @Override
        public int getCount() {
            return foods.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        pageAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        pageAdapter.addTabs(mDiningHall);
        pager = (ViewPager) v.findViewById(R.id.menu_pager);
        pager.setAdapter(pageAdapter);
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
        ((MainActivity) getActivity()).addTabs(pageAdapter, pager, true);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining, menu);
        super.onCreateOptionsMenu(menu, inflater);

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
        mActivity.removeTabs();
    }
    private String getCurrentTab() {
        try {
            ViewFlipper flipper = (ViewFlipper) pager.getChildAt(pager.getCurrentItem());
            WebView tab = (WebView) ((NestedScrollView) flipper.getChildAt(1)).getChildAt(0);
            return tab.getUrl();
        } catch (Exception e) {
            return null;
        }
    }
}