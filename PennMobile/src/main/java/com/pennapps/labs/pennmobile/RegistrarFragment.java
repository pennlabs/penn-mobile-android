package com.pennapps.labs.pennmobile;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ArrayRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Jason on 1/25/2016.
 */
public class RegistrarFragment extends SearchFavoriteFragment {

    private RegistrarTabAdapter adapter;

    protected class RegistrarTabAdapter extends ListTabAdapter {

        RegistrarTab[] array;

        public RegistrarTabAdapter(FragmentManager fm) {
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
            adapter = new RegistrarTabAdapter(mActivity.getSupportFragmentManager());
        }
        return adapter;
    }

    @Override
    protected @ArrayRes int previousArrayKey() {
        return R.array.previous_course_array;
    }

    @Override
    protected int searchCount() {
        return PreferenceManager.getDefaultSharedPreferences(mActivity).getInt(getString(R.string.registrar_search_count), -1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Courses");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature");
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.removeTabs();
        getActivity().setTitle(R.string.registrar);
        if (Build.VERSION.SDK_INT > 17){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setSelectedTab(6);
        }
    }

    @Override
    protected String getTitle() {
        return getString(R.string.registrar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
