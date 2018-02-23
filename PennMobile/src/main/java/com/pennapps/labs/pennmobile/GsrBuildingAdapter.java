package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.classes.GSR;
import com.pennapps.labs.pennmobile.classes.GSRContainer;
import com.pennapps.labs.pennmobile.classes.GSRContainerSlot;
import com.pennapps.labs.pennmobile.classes.GSRSlot;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/5/2017.
 * RecylerView Adapter for a specific building with GSRs.
 */

public class GsrBuildingAdapter extends RecyclerView.Adapter<GsrBuildingHolder> {

    Context context;
    ArrayList<GSRContainer> gsrs;
    String gsrLocationCode;

    public GsrBuildingAdapter(Context context, ArrayList<GSRContainer> _gsrs, String _gsrLocationCode) {
        this.context = context;
        this.gsrs = _gsrs;
        this.gsrLocationCode = _gsrLocationCode;
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

                //now define arrays
                ArrayList<String> times = new ArrayList<String>();
                ArrayList<String> dates = new ArrayList<String>();
                ArrayList<String> ids = new ArrayList<String>();

                for (int j = 0; j < gsrs.get(position).getAvailableGSRSlots().size(); j++)
                {
                    GSRContainerSlot gsrslot = gsrs.get(position).getAvailableGSRSlots().get(j);
                    times.add(gsrslot.getTimeRange());
                    dates.add(gsrslot.getDateNum());
                    ids.add(gsrslot.getElementId());
                }

                gsrRoomsRecyclerView.setAdapter(new GsrRoomAdapter(times, ids, gsrLocationCode, context, dates));
                holder.gsrBuildingName.setText(gsrs.get(position).getGsrName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return gsrs.size();
    }
}
