package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for individual GSR rooms.
 */

public class GsrRoomHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.gsr_time) TextView gsrTime;
    @Bind(R.id.gsr_id)  TextView gsrId;
    @Bind(R.id.locationId) TextView locationId;

    public GsrRoomHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}
