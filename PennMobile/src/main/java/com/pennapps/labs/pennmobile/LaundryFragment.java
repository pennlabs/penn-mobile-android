package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryHallAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryHall;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;


public class LaundryFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private MainActivity mActivity;
    private final static int ROW_CAP = 15;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
    SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry, container, false);
        ButterKnife.bind(this, v);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.laundry_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLaundryHall();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);
        getLaundryHall();
        return v;
    }

    private void getLaundryHall() {
        mLabs.laundries()
                .subscribe(new Action1<List<LaundryRoom>>() {
                    @Override
                    public void call(final List<LaundryRoom> rooms) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    List<LaundryHall> halls = new ArrayList<>(LaundryHall.getLaundryHall(rooms));
                                    List<LaundryHall> hallsOrdered = new ArrayList<>();
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    Iterator<LaundryHall> iter = halls.iterator();
                                    while(iter.hasNext()){
                                        LaundryHall next = iter.next();
                                        if(sp.getBoolean(next.getName() + "_isFavorite",false)){
                                            hallsOrdered.add(next);
                                            iter.remove();
                                        }
                                    }
                                    hallsOrdered.addAll(halls);
                                    LaundryHallAdapter adapter = new LaundryHallAdapter(mActivity, hallsOrdered);
                                    mListView.setAdapter(adapter);
                                    loadingPanel.setVisibility(View.GONE);
                                    no_results.setVisibility(View.GONE);
                                    Bundle args = getArguments();
                                    if (args != null) {
                                        int hall_no = args.getInt(getString(R.string.laundry_hall_no), -1);
                                        if (hall_no != -1) {
                                            for (LaundryHall hall : hallsOrdered) {
                                                for (LaundryRoom laundryRoom : hall.getIds()) {
                                                    if (laundryRoom.hall_no == hall_no) {
                                                        toLaundryHall(hall);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e){
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e){
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        LaundryHallAdapter adapter = (LaundryHallAdapter) l.getAdapter();
        LaundryHall lh = adapter.getItem(position);
        toLaundryHall(lh);
    }

    private void toLaundryHall(LaundryHall lh) {
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Bundle args = new Bundle();
        if (lh.getIds().size() >= 1) {
            Fragment fragment;
            if (lh.getIds().size() == 1) {
                fragment = new LaundryMachineFragment();
                args.putParcelable(getString(R.string.laundry), lh.getIds().get(0));
            } else {
                fragment = new LaundryBuildingFragment();
                args.putParcelable(getString(R.string.laundry_hall_arg), lh);
                if (getArguments() != null) {
                    int hall_no = getArguments().getInt(getString(R.string.laundry_hall_no), -1);
                    if (hall_no != -1) {
                        args.putInt(getString(R.string.laundry_hall_no), hall_no);
                        getArguments().putInt(getString(R.string.laundry_hall_no), -1);
                    }
                }
            }
            fragment.setArguments(args);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, "TAG1")
                    .addToBackStack("Laundry Main")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setTitle(R.string.laundry);
        mActivity.setNav(R.id.nav_laundry);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public static void setSummary(int avail, int used, int idoffset, RelativeLayout rl, Context context) {
        LinkedList<ImageView> vertical = new LinkedList<>();
        ImageView prev = null;
        int max_col = ROW_CAP;
        if (avail + used > ROW_CAP) {
            max_col = (avail + used) / 2;
        }
        for (int i = 0; i < avail + used; i++) {
            RelativeLayout.LayoutParams layparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (i < max_col) {
                layparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else if (i % max_col == 0) {
                layparam.addRule(RelativeLayout.BELOW, vertical.getLast().getId());
            } else {
                layparam.addRule(RelativeLayout.BELOW, vertical
                        .get(vertical.size() - 2).getId());
            }
            if (i % max_col == 0) {
                layparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else if (prev != null) {
                layparam.addRule(RelativeLayout.RIGHT_OF, prev.getId());
            }
            layparam.setMargins(0, 0, 7, 5);
            ImageView imageView = new ImageView(context);
            if (i < avail) {
                imageView.setImageResource(R.drawable.green_circle);
            } else {
                imageView.setImageResource(R.drawable.red_circle);
            }
            imageView.setId((idoffset + 1) * 300 + i);
            rl.addView(imageView, layparam);
            if (i % max_col == 0) {
                vertical.add(imageView);
            }
            prev = imageView;
        }
    }
}
