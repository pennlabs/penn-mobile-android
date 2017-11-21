package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Jackie on 2017-11-20.
 */

public class LaundryOnboardingFragmentAdapter extends FragmentPagerAdapter {

    public LaundryOnboardingFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0) {
            bundle.putInt("page", position);
            Fragment fragment = new LaundryOnboardingFragment();
            fragment.setArguments(bundle);
            return fragment;
        } else if (position == 1) {
            bundle.putInt("page", position);
            Fragment fragment = new LaundryOnboardingFragment();
            fragment.setArguments(bundle);
            return fragment;
        } else {
            bundle.putInt("page", position);
            Fragment fragment = new LaundryOnboardingFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
