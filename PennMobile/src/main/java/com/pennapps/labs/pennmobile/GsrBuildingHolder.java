package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for a specific building with GSRs.
 */

public class GsrBuildingHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.gsr_building_name) TextView gsrBuildingName;
    @Bind(R.id.gsr_availability_in_building) RecyclerView recyclerView;

    public GsrBuildingHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
