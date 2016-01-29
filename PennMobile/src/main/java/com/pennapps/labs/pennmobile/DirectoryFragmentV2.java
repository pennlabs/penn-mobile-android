package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Jason on 1/26/2016.
 */
public class DirectoryFragmentV2 extends SearchFavoriteFragment {

    protected class DirectoryAdapter extends ListTabAdapter {

        SearchFavoriteTab[] array;

        public DirectoryAdapter(FragmentManager fm) {
            super(fm);
            array = new SearchFavoriteTab[2];
        }

        @Override
        public void onReceiveQuery(String query) {
            array[0].processQuery(query);
            setIndex(R.string.directory_search_count, R.array.previous_directory_array, query);
        }

        @Override
        public Fragment getItem(int position) {
            if (array[position] == null) {
                SearchFavoriteTab fragment = new SearchFavoriteTab();
                Bundle args = new Bundle();
                args.putBoolean(getString(R.string.search_favorite), position == 1);
                args.putString(getString(R.string.search_list), getString(R.string.directory));
                fragment.setArguments(args);
                array[position] = fragment;
            }
            return array[position];
        }
    }

    @Override
    protected ListTabAdapter getAdapter() {
        return new DirectoryAdapter(mActivity.getSupportFragmentManager());
    }

    @Override
    protected int previousArrayKey() {
        return R.array.previous_directory_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.directory_search_count), -1);
    }
}
