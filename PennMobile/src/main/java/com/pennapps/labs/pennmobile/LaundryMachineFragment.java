package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Laundry;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Jason on 11/4/2015.
 */
public class LaundryMachineFragment extends Fragment {
    private Labs mLabs;
    private MainActivity mActivity;
    private Laundry laundry;
    private List<LaundryMachine> machines;
    private TabAdapter pageAdapter;
    private ViewPager pager;

    class TabAdapter extends FragmentStatePagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = new LaundryMachineTab(); //data stored into the parent fragment with Fragment.getParent();
            Bundle args = new Bundle();
            args.putInt(getString(R.string.laundry_position), position);
            args.putParcelable(getString(R.string.laundry), laundry);
            if(machines != null) {
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
        public CharSequence getPageTitle(int position){
            if(position == 0){
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
        laundry = args.getParcelable(getString(R.string.laundry));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).closeKeyboard();
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
        ((MainActivity) getActivity()).addLaundryTabs(pageAdapter, pager);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(laundry.name != null) {
            getActivity().setTitle(laundry.name);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ((MainActivity) getActivity()).removeTabs();
        ButterKnife.unbind(this);
    }
}
