package com.pennapps.labs.pennmobile;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.adapters.DiningAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DiningFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private Activity mActivity;
    public static Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        mActivity = getActivity();
        mFragment = this;
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        getDiningHalls();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dining, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DiningHall diningHall = ((DiningAdapter.ViewHolder) v.getTag()).hall;
        if (diningHall.hasMenu()) {
            Fragment fragment = new MenuFragment();

            Bundle args = new Bundle();
            args.putParcelable("DiningHall", ((DiningAdapter.ViewHolder) v.getTag()).hall);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.dining_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
            onResume();
        }
    }

    private void getDiningHalls() {
        mLabs.venues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                new Action1<List<Venue>>() {
                    @Override
                    public void call(List<Venue> venues) {
                        ArrayList<DiningHall> diningHalls = new ArrayList<>();
                        for (Venue venue : venues) {
                            DiningHall hall = new DiningHall(venue.id, venue.name, venue.isResidential(), venue.hasMenu(mLabs), venue.getHours());
                            diningHalls.add(hall);
                            if (hall.isResidential() && hall.hasMenu()) {
                                NewDiningHall newDiningHall = mLabs.daily_menu(hall.getId());
                                hall.parseMeals(newDiningHall);
                            }
                        }
                        try {
                            DiningAdapter mAdapter = new DiningAdapter(mActivity, diningHalls);
                            mListView.setAdapter(mAdapter);
                            getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        } catch (NullPointerException ignored) {

                        }
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showErrorToast();
                    }
                });
    }

    private void showErrorToast() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, R.string.no_results, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
    }
}
