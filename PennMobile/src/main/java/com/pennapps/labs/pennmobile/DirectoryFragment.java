package com.pennapps.labs.pennmobile;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ArrayRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.Unbinder;

public class DirectoryFragment extends SearchFavoriteFragment {

    private DirectoryTabAdapter adapter;
    private Unbinder unbinder;

    protected class DirectoryTabAdapter extends ListTabAdapter {

        DirectoryTab[] array;

        public DirectoryTabAdapter(FragmentManager fm) {
            super(fm);
            array = new DirectoryTab[2];
        }

        @Override
        public boolean onReceiveQuery(String query) {
            if (array[0] != null) {
                array[0].processQuery(query);
                setIndex(R.string.directory_search_count, R.array.previous_directory_array, query);
            }
            return array[0] != null;
        }

        @Override
        public Fragment getItem(int position) {
            if (array[position] == null) {
                DirectoryTab fragment = new DirectoryTab();
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
        if (adapter == null){
            adapter = new DirectoryTabAdapter(mActivity.getSupportFragmentManager());
        }
        return adapter;
    }

    @Override
    protected @ArrayRes int previousArrayKey() {
        return R.array.previous_directory_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.directory_search_count), -1);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.directory);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "6");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Directory");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature");
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).removeTabs();
        getActivity().setTitle(R.string.directory);
        if (Build.VERSION.SDK_INT > 17){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setSelectedTab(7);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
