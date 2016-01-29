package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Jason on 1/25/2016.
 */
public class RegistrarFragmentV2 extends SearchFavoriteFragment {

    protected class RegistrarAdapter extends ListTabAdapter {

        SearchFavoriteTab[] array;

        public RegistrarAdapter(FragmentManager fm) {
            super(fm);
            array = new SearchFavoriteTab[2];
        }

        @Override
        public void onReceiveQuery(String query) {
            array[0].processQuery(query);
            setIndex(R.string.registrar_search_count, R.array.previous_course_array, query);
        }

        @Override
        public Fragment getItem(int position) {
            if (array[position] == null) {
                SearchFavoriteTab fragment = new SearchFavoriteTab();
                Bundle args = new Bundle();
                args.putBoolean(getString(R.string.search_favorite), position == 1);
                args.putString(getString(R.string.search_list), getString(R.string.registrar));
                fragment.setArguments(args);
                array[position] = fragment;
            }
            return array[position];
        }
    }
    @Override
    protected ListTabAdapter getAdapter() {
        return new RegistrarAdapter(mActivity.getSupportFragmentManager());
    }

    @Override
    protected int previousArrayKey() {
        return R.array.previous_course_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.registrar_search_count), -1);
    }
}
