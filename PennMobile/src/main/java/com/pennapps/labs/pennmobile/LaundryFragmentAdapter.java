package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryFragmentAdapter extends FragmentPagerAdapter {

    Context mContext;
    String washers;
    String dryers;
    private String[] fragmentNames;

    public LaundryFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        washers = mContext.getString(R.string.laundry_washer);
        dryers = mContext.getString(R.string.laundry_dryer);
        fragmentNames = new String[]{washers, dryers};
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            bundle.putString(mContext.getString(R.string.laundry_machine_type), mContext.getString(R.string.washer));
            Fragment fragment = new LaundryMachineFragment();
            fragment.setArguments(bundle);
            return fragment;
        } else {
            bundle.putString(mContext.getString(R.string.laundry_machine_type), mContext.getString(R.string.dryer));
            Fragment fragment = new LaundryMachineFragment();
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
