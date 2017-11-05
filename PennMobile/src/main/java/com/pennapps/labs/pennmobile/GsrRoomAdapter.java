package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andrew on 11/5/2017.
 * RecylerView Adapter for individual GSR rooms.
 */

public class GsrRoomAdapter extends RecyclerView.Adapter<GsrRoomHolder> {
    @Override
    public GsrRoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gsr_room, parent, false);
        return new GsrRoomHolder(view);
    }

    @Override
    public void onBindViewHolder(GsrRoomHolder holder, int position) {
        if (position < getItemCount()) {
            int ampm = (position >= 5) ? 1 : 0;
            String[] startEndTimes = gsrFragment.getStartEndTimes((position + 6) % 12 + 1, position, ampm);
            holder.startTime.setText(startEndTimes[0]);
            holder.endTime.setText(startEndTimes[1]);
        }
    }

    @Override
    public int getItemCount() {
        return 12;
    }
}
