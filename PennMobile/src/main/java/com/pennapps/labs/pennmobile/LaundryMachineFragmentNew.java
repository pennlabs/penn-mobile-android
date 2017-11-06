package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapterNew;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryMachineFragmentNew extends android.support.v4.app.Fragment {

    private Labs mLabs;
    private Context mContext;

    private SharedPreferences sp;

    // views
    @Bind(R.id.laundry_help_text)
    TextView mTextView;
    @Bind(R.id.loadingPanel)
    RelativeLayout loadingPanel;
    @Bind(R.id.no_results)
    TextView no_results;
    @Bind(R.id.favorite_laundry_list)
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // list of favorite laundry rooms
    private ArrayList<LaundryRoom> laundryRooms = new ArrayList<>();

    private LaundryRoomAdapterNew mAdapter;

    // washer or dryer
    private String machineType;

    // empty constructor
    public LaundryMachineFragmentNew() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mContext = getActivity();
        machineType = getArguments().getString("machineType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laundry_machine_new, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.laundry_machine_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRooms();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        updateRooms();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateRooms() {

        laundryRooms.clear();
        boolean addedRoom = false;

        Intent mIntent = getActivity().getIntent();
        int numRooms = mIntent.getIntExtra("numRooms", 48);
        for (int i = 0; i < numRooms; i++) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                addRoom(i);
                addedRoom = true;
            }
        }

        if (!addedRoom) {
            loadingPanel.setVisibility(View.GONE);
        }
    }

    private void addRoom(int i) {
        mLabs.room(i)
                .subscribe(new Action1<LaundryRoom>() {
                    @Override
                    public void call(final LaundryRoom room) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    laundryRooms.add(room);

                                    mAdapter = new LaundryRoomAdapterNew(mContext, laundryRooms, machineType);
                                    mRecyclerView.setAdapter(mAdapter);

                                    loadingPanel.setVisibility(View.GONE);
                                    mTextView.setVisibility(View.INVISIBLE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
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
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }
}