package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class DirectoryFragment extends SearchFavoriteFragment {

    private DirectoryTabAdapter adapter;

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
        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Directory")
                .putContentType("App Feature")
                .putContentId("6"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.directory);
        mActivity.setNav(R.id.nav_directory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
