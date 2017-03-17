package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryMachineAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class LaundryMachineTab extends ListFragment {

    private Labs mLabs;
    private MainActivity mActivity;
    private LaundryRoom laundryRoom;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<LaundryMachine> machines;
    private ListView mListView;
    private boolean wash;
    private int[] laundryTraffic;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        laundryRoom = getArguments().getParcelable(getString(R.string.laundry));
        wash = getArguments().getInt(getString(R.string.laundry_position), 0) == 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.laundry_machine_tab, container, false);
        ButterKnife.bind(this, v);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.laundry_machine_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMachines();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);
        if (getArguments() != null && getArguments().getParcelableArray(getString(R.string.laundry_machine_intent)) != null) {
            LaundryMachine[] array = (LaundryMachine[])getArguments()
                    .getParcelableArray(getString(R.string.laundry_machine_intent));
            if (array != null) {
                machines = new LinkedList<>(Arrays.asList(array));
            } else {
                machines = new LinkedList<>();
            }
        }
        if (machines == null || machines.isEmpty()) {
            getMachines();
            getMachinesUsages();
        } else {
            v.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            setMachines(machines, laundryRoom);
        }
        return v;
    }

    private void getMachines() {
        mLabs.laundryMachines(laundryRoom.hall_no)
                .subscribe(new Action1<List<LaundryMachine>>() {
                    @Override
                    public void call(final List<LaundryMachine> machines) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                try {
                                    setMachines(machines, laundryRoom);
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException ignore) {
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
                                } catch (NullPointerException ignore) {
                                    //it has gone to another page
                                }
                            }
                        });
                    }
                });
    }

    private void getMachinesUsages() {
        mLabs.laundryMachinesUsage(laundryRoom.hall_no)
                .subscribe(new Action1<LaundryUsage>() {
                    @Override
                    public void call(final LaundryUsage usage) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Calendar sCalendar = Calendar.getInstance();
                                    String currentDay = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                                    laundryTraffic = new int[24];
                                    String[] day;
                                    if (currentDay.equals("Friday")) {
                                        day = usage.Friday;
                                    } else if (currentDay.equals("Saturday")) {
                                        day = usage.Saturday;
                                    } else if (currentDay.equals("Sunday")) {
                                        day = usage.Sunday;
                                    } else if (currentDay.equals("Monday")) {
                                        day = usage.Monday;
                                    } else if (currentDay.equals("Tuesday")) {
                                        day = usage.Tuesday;
                                    } else if (currentDay.equals("Wednesday")) {
                                        day = usage.Wednesday;
                                    } else {
                                        day = usage.Thursday;
                                    }
                                    for (int i = 0; i < laundryTraffic.length; i++) {
                                        if (day[i].equals("High")) {
                                            laundryTraffic[i] = 3;
                                        } else if (day[i].equals("Medium")) {
                                            laundryTraffic[i] = 2;
                                        } else {
                                            laundryTraffic[i] = 1;
                                        }
                                    }
                                } catch (NullPointerException ignore) {
                                    //it has gone to another page.
                                    Log.d("error!", "lul", ignore);
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
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
                                    Log.d("rip", "thrown", throwable);
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException ignore) {
                                    //it has gone to another page
                                }
                            }
                        });
                    }
                });
    }

    private void setMachines(List<LaundryMachine> machines, LaundryRoom laundryRoom) {
        this.machines = machines;
        LinkedList<LaundryMachine> filtered = new LinkedList<>();
        String type;
        if (wash) {
            type =  getString(R.string.laundry_washer_textview);
        } else {
            type = getString(R.string.laundry_dryer_textview);
        }
        for (LaundryMachine machine: machines) {
            if(machine.machine_type.contains(type)){
                filtered.add(machine);
            }
        }
        LaundryMachineAdapter adapter = new LaundryMachineAdapter(mActivity, filtered, wash, laundryTraffic, laundryRoom);
        mListView.setAdapter(adapter);
    }

    public List<LaundryMachine> returnMachines() {
        return machines;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
