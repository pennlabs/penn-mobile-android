package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.FlingPerformanceViewHolder;

import java.util.ArrayList;

public class FlingRecyclerViewAdapter extends RecyclerView.Adapter<FlingPerformanceViewHolder> {

    Context context;
    ArrayList<String> data;

    public FlingRecyclerViewAdapter(Context context, ArrayList<String> sampleData) {
        this.context = context;
        data = sampleData;
    }

    @Override
    public FlingPerformanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fling_performance_item, parent, false);
        return new FlingPerformanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlingPerformanceViewHolder holder, int position) {
        if (position < getItemCount()) {
            holder.flingview_name.setText("Performer: " + data.get(position) + ", number " + position);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
