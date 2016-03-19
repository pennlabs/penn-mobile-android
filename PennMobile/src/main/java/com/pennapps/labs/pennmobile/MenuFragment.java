package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class MenuFragment extends Fragment {

    TabAdapter pageAdapter;
    ViewPager pager;

    private DiningHall mDiningHall;
    private MainActivity mActivity;

    class TabAdapter extends FragmentStatePagerAdapter {

        ArrayList<HashMap<String, ArrayList<String>>> foods;  //for each meal: {name of station: arraylist of foods at the station}
        ArrayList<String> headers;
        String name;

        public TabAdapter(FragmentManager fm) {
            super(fm);
            foods = new ArrayList<HashMap<String, ArrayList<String>>>();
            headers = new ArrayList<>();
        }

        private void addTabs(DiningHall hall) {
            List<DiningHall.Menu> menus = hall.menus;
            name = hall.getName();
            headers.add("HOURS");
            foods.add(new HashMap<String, ArrayList<String>>());    //first menu is empty for dining hall info tab
            for (DiningHall.Menu menu: menus) {
                HashMap<String, ArrayList<String>> stations = new HashMap<String, ArrayList<String>>();
                headers.add(menu.name);
                for (DiningHall.DiningStation station : menu.stations) {
                    ArrayList<String> foods = new ArrayList<>();
                    StringBuilder foodItems = new StringBuilder();      //for design purposes
                    for (int i=0; i<station.items.size(); i++){
                        String txt = station.items.get(i).title;
                        foodItems.append(String.valueOf(txt.charAt(0)).toUpperCase());
                        foodItems.append(txt.substring(1, txt.length()));
                        if (i<station.items.size()-1) {
                            foodItems.append("\n");
                        }
                    }

                    foods.add(foodItems.toString());
                    stations.put(StringUtils.capitalize(station.name), foods);
                }
                foods.add(stations);
            }
        }


        @Override
        public Fragment getItem(int position) {
            Fragment myFragment;
            if (position==0){
                myFragment = new DiningInfoFragment();
                Bundle args = new Bundle();
                args.putParcelable("DiningHall",mDiningHall);
                args.putString("name", name);
                args.putString("type","info");
                myFragment.setArguments(args);
            }
            else{
                myFragment = new MenuTab();
                Bundle args = new Bundle();
                args.putString("type","menu");
                args.putString("name",name);
                args.putStringArrayList("stations", new ArrayList<String>(foods.get(position).keySet()));
                HashMap<String, ArrayList<String>> stations = foods.get(position);
                for (String station: stations.keySet()){
                    args.putStringArrayList(station, stations.get(station));
                }
                myFragment.setArguments(args);
            }
            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers.get(position);
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

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        mActivity.setTitle(mDiningHall.getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.dining);
        mActivity.removeTabs();
        ButterKnife.unbind(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.removeTabs();
    }
}