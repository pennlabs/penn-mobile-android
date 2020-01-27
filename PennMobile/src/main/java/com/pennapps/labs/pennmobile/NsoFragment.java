package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import androidx.annotation.ArrayRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Jason on 8/6/2016.
 */

public class NsoFragment extends SearchFavoriteFragment {

    private NsoTabAdapter adapter;

    protected class NsoTabAdapter extends ListTabAdapter {

        NsoTab[] array;

        public NsoTabAdapter(FragmentManager fm) {
            super(fm);
            array = new NsoTab[2];
        }

        @Override
        public boolean onReceiveQuery(String query) {
            if (array[0] != null) {
                array[0].processQuery(query);
                setIndex(R.string.nso_search_count, R.array.previous_nso_array, query);
            }
            return array[0] != null;
        }

        @Override
        public Fragment getItem(int position) {
            if (array[position] == null) {
                NsoTab fragment = new NsoTab();
                Bundle args = new Bundle();
                args.putBoolean(getString(R.string.search_favorite), position == 1);
                args.putString(getString(R.string.search_list), getString(R.string.nso));
                fragment.setArguments(args);
                array[position] = fragment;
            }
            return array[position];
        }
    }

    protected ListTabAdapter getAdapter() {
        if (adapter == null){
            adapter = new NsoTabAdapter(mActivity.getSupportFragmentManager());
        }
        return adapter;
    }

    @Override
    protected @ArrayRes
    int previousArrayKey() {
        return R.array.previous_nso_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.nso_search_count), -1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "8");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "NSO");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature");
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nso);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.nso);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    protected void fabInit(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                lastQuery = "";
                hideSuggestion();
                adapter.onReceiveQuery("");
            }
        });
    }
}
