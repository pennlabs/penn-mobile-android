package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.List;

import butterknife.ButterKnife;

public class LaundryMachineFragment extends Fragment {
    private Labs mLabs;
    private MainActivity mActivity;
    private LaundryRoom laundryRoom;
    private List<LaundryMachine> machines;
    private TabAdapter pageAdapter;
    private ViewPager pager;

    private boolean favoriteState = false;

    class TabAdapter extends FragmentStatePagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = new LaundryMachineTab(); //data stored into the parent fragment with Fragment.getParent();
            Bundle args = new Bundle();
            args.putInt(getString(R.string.laundry_position), position);
            args.putParcelable(getString(R.string.laundry), laundryRoom);
            if (machines != null) {
                LaundryMachine[] array = (LaundryMachine[]) machines.toArray();
                args.putParcelableArray(getString(R.string.laundry_machine_intent), array);
            }
            myFragment.setArguments(args);
            return myFragment;
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.laundry_washer);
            }
            return getString(R.string.laundry_dryer);
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        Bundle args = getArguments();
        laundryRoom = args.getParcelable(getString(R.string.laundry));
        setHasOptionsMenu(true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        favoriteState = sp.getBoolean(laundryRoom.name + "_isFavorite", false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.closeKeyboard();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry_machine, container, false);
        ButterKnife.bind(this, v);
        pageAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                machines = ((LaundryMachineTab) ((FragmentStatePagerAdapter) pager.getAdapter()).getItem(position)).returnMachines();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        v.setBackgroundColor(Color.WHITE);
        mActivity.addTabs(pageAdapter, pager, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (laundryRoom.name != null) {
            mActivity.setTitle(laundryRoom.name);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.setTitle(R.string.laundry);
        mActivity.removeTabs();
        ButterKnife.unbind(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(laundryRoom.name + "_isFavorite", favoriteState);
        editor.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.laundry_favorite, menu);
        if(favoriteState){
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_star_white_48dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if(!favoriteState) {
                    item.setIcon(R.drawable.ic_star_white_48dp);
                } else {
                    item.setIcon(R.drawable.ic_star_border_white_48dp);
                }
                favoriteState = !favoriteState;

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
