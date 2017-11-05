package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for individual GSR rooms.
 */

public class GsrRoomHolder extends RecyclerView.ViewHolder {

    TextView startTime, endTime;

    public GsrRoomHolder(View itemView) {
        super(itemView);
        startTime = (TextView) itemView.findViewById(R.id.start_time);
        endTime = (TextView) itemView.findViewById(R.id.end_time);
    }
}
