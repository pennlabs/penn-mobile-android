package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for individual GSR rooms.
 */

public class GsrRoomHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.gsr_room) LinearLayout gsrRoom;
    @BindView(R.id.gsr_start_time) TextView gsrStartTime;
    @BindView(R.id.gsr_end_time) TextView gsrEndTime;
    @BindView(R.id.gsr_id)  TextView gsrId;
    @BindView(R.id.locationId) TextView locationId;

    public GsrRoomHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}
