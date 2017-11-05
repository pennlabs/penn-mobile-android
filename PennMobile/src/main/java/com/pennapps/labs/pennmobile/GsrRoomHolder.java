package com.pennapps.labs.pennmobile;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for individual GSR rooms.
 */

public class GsrRoomHolder extends RecyclerView.ViewHolder {

    TextView gsr_time, gsrId, locationId;

    public GsrRoomHolder(View itemView) {
        super(itemView);
        gsr_time = (TextView) itemView.findViewById(R.id.gsr_time);
        gsrId = (TextView) itemView.findViewById(R.id.gsr_id);
        locationId = (TextView) itemView.findViewById(R.id.locationId);

    }


}
