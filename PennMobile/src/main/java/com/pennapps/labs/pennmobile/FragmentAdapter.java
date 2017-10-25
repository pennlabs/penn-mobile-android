package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Jackie on 2017-10-22.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter (FragmentManager fm) {
        super(fm);
    }

    private String[] fragmentNames = new String[]{"Washers", "Dryers"};

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            bundle.putString("machineType", "washer");
            Fragment fragment = new LaundryMachineFragmentNew();
            fragment.setArguments(bundle);
            return fragment;
        }
        else {
            bundle.putString("machineType", "dryer");
            Fragment fragment = new LaundryMachineFragmentNew();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames[position];
    }
}
