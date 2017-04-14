package com.pennapps.labs.pennmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew on 3/24/2017.
 */

public class LaundryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LaundryMachine> machines;
    private MainActivity activity;
    private boolean wash;
    private LaundryRoom laundryRoom;
    private int[] traffic;

    public LaundryRecyclerAdapter(MainActivity activity, LaundryRoom laundryRoom) {
        super();
        this.machines = new LinkedList<>();
        this.activity = activity;
        wash = false;
        this.laundryRoom = laundryRoom;
        traffic = new int[24];
    }

    public LaundryRecyclerAdapter(MainActivity activity, List<LaundryMachine> machines, boolean wash, int[] laundryTraffic, LaundryRoom laundryRoom) {
        super();
        this.machines = machines;
        this.activity = activity;
        this.wash = wash;
        this.laundryRoom = laundryRoom;
        traffic = laundryTraffic;
        smoothTrafficData();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void smoothTrafficData() {
        int[] copyTraffic = new int[traffic.length];
        for (int i = 0; i < copyTraffic.length; i++) {
            copyTraffic[i] = traffic[i];
        }
        for (int i = 1; i < traffic.length - 1; i++) {
            if (Math.abs(copyTraffic[i] - (copyTraffic[i - 1] + copyTraffic[i + 1]) / 2) <=
                    Math.abs(copyTraffic[i - 1] - copyTraffic[i + 1]) / 2) {
                traffic[i] = (copyTraffic[i - 1] + copyTraffic[i + 1]) / 2;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        switch (viewType) {
            case 0:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_summary_item, parent, false);
                return new SummaryHolder(inflatedView);
            case 1:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
                return new MachineHolder(inflatedView, activity);
        }
        inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        return new MachineHolder(inflatedView, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                SummaryHolder summaryHolder = (SummaryHolder) holder;
                summaryHolder.bindSummary(activity, laundryRoom, machines, wash, traffic);
                break;
            case 1:
                if (position <= machines.size()) {
                    LaundryMachine ms = machines.get(position - 1);
                    MachineHolder machineHolder = (MachineHolder) holder;
                    machineHolder.bindMachineStatusHolder(ms, laundryRoom);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return machines.size() + 1;
    }
}
