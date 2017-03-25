package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryHall;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static android.content.ContentValues.TAG;

public class LaundryBuildingFragment extends ListFragment {

    private MainActivity mActivity;
    private Labs mLabs;
    private LaundryHall lh;
    private LaundryRoomAdapter adapter;
    private List<LaundryRoom> laundriesOrdered;
    @Bind(R.id.no_results) TextView no_results;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        lh = getArguments().getParcelable(getString(R.string.laundry_hall_arg));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry_building, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        if (lh != null) {
            List<LaundryRoom> laundries = new ArrayList<>(lh.getIds());
            laundriesOrdered = new ArrayList<>();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Iterator<LaundryRoom> iter = laundries.iterator();
            while(iter.hasNext()){
                LaundryRoom next = iter.next();
                if(sp.getBoolean(next.name + "_isFavorite",false)){
                    laundriesOrdered.add(next);
                    iter.remove();
                }
            }
            laundriesOrdered.addAll(laundries);
            adapter = new LaundryRoomAdapter(mActivity, laundriesOrdered);
            setListAdapter(adapter);
            no_results.setVisibility(View.GONE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            for(int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++){
                Log.i(TAG, "Found fragment: " + fragmentManager.getBackStackEntryAt(entry).getName());
            }
        }
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.laundry_building_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRefreshedLaundryHall();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);
        getRefreshedLaundryHall();
        return v;
    }

    private void getRefreshedLaundryHall() {
        mLabs.laundries()
                .subscribe(new Action1<List<LaundryRoom>>() {
                    @Override
                    public void call(final List<LaundryRoom> rooms) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<LaundryHall> halls = new ArrayList<LaundryHall>(LaundryHall.getLaundryHall(rooms));
                                for (LaundryHall hall : halls) {
                                    if (hall.getName().equals(lh.getName())) {
                                        lh = hall;
                                        adapter = new LaundryRoomAdapter(mActivity, laundriesOrdered);
                                        setListAdapter(adapter);
                                        break;
                                    }
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                },  new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //do nothing because the view without refresh is our best bet
                        try {
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (NullPointerException e){
                            //it has gone to another page.
                        }
                    }
                });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        LaundryRoom laundryRoom = laundriesOrdered.get(position);
        toLaundryMachine(laundryRoom);
    }

    private void toLaundryMachine(LaundryRoom laundryRoom) {
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Fragment fragment = new LaundryMachineFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.laundry), laundryRoom);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack("Laundry Building List")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        if (lh != null) {
            getActivity().setTitle(lh.getName());
        }
        if (getArguments() != null) {
            int hall_no = getArguments().getInt(getString(R.string.laundry_hall_no), -1);
            if (hall_no != -1) {
                for (LaundryRoom laundryRoom : laundriesOrdered) {
                    if (laundryRoom.hall_no == hall_no) {
                        getArguments().putInt(getString(R.string.laundry_hall_no), -1);
                        toLaundryMachine(laundryRoom);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
