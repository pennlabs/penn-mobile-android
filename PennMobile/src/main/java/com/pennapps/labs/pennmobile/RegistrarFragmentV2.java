package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import butterknife.ButterKnife;

/**
 * Created by Jason on 1/25/2016.
 */
public class RegistrarFragmentV2 extends SearchFavoriteFragment {

    private RegistrarAdapter adapter;

    protected class RegistrarAdapter extends ListTabAdapter {

        RegistrarTab[] array;

        public RegistrarAdapter(FragmentManager fm) {
            super(fm);
            array = new RegistrarTab[2];
        }

        @Override
        public boolean onReceiveQuery(String query) {
            if (array[0] != null) {
                array[0].processQuery(query);
                setIndex(R.string.registrar_search_count, R.array.previous_course_array, query);
            }
            return array[0] != null;
        }

        @Override
        public Fragment getItem(int position) {
            if (array[position] == null) {
                RegistrarTab fragment = new RegistrarTab();
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
        if (adapter == null){
            adapter = new RegistrarAdapter(mActivity.getSupportFragmentManager());
        }
        return adapter;
    }

    @Override
    protected int previousArrayKey() {
        return R.array.previous_course_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.registrar_search_count), -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registrar);
        mActivity.setNav(R.id.nav_registrar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
