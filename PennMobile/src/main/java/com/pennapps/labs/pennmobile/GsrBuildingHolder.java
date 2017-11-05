package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Andrew on 11/5/2017.
 * RecyclerView ViewHolder for a specific building with GSRs.
 */

public class GsrBuildingHolder extends RecyclerView.ViewHolder {

    TextView gsrBuildingName;
    RecyclerView recyclerView;

    public GsrBuildingHolder(View itemView) {
        super(itemView);
        gsrBuildingName = (TextView) itemView.findViewById(R.id.gsr_building_name);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.gsr_availability_in_building);
    }
}
