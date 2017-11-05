package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andrew on 11/5/2017.
 * RecylerView Adapter for a specific building with GSRs.
 */

public class GsrBuildingAdapter extends RecyclerView.Adapter<GsrBuildingHolder> {

    Context context;


    public GsrBuildingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public GsrBuildingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gsr_building, parent, false);
        return new GsrBuildingHolder(view);
    }

    @Override
    public void onBindViewHolder(GsrBuildingHolder holder, int position) {
        if (position < getItemCount()) {
            RecyclerView gsrRoomsRecyclerView = holder.recyclerView;
            if (gsrRoomsRecyclerView != null) {
                LinearLayoutManager gsrRoomsLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                gsrRoomsRecyclerView.setLayoutManager(gsrRoomsLayoutManager);
                gsrRoomsRecyclerView.setAdapter(new GsrRoomAdapter());
                holder.gsrBuildingName.setText("Building number " + position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
