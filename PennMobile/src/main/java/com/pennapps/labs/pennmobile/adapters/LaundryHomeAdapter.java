package com.pennapps.labs.pennmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.MachineList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2018-03-14.
 */

public class LaundryHomeAdapter extends RecyclerView.Adapter<LaundryHomeAdapter.CustomViewHolder> {

    List<LaundryRoom> mLaundryRooms;

    public LaundryHomeAdapter(List<LaundryRoom> laundryRooms) {
        mLaundryRooms = laundryRooms;
    }

    @Override
    public com.pennapps.labs.pennmobile.adapters.LaundryHomeAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cardview_item_laundry, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.pennapps.labs.pennmobile.adapters.LaundryHomeAdapter.CustomViewHolder holder, int position) {
        // washer and dryer availability summary
        LaundryRoom room = mLaundryRooms.get(position);
        MachineList washers = room.getMachines().getWashers();
        MachineList dryers = room.getMachines().getDryers();
        int washersOpen = washers.getOpen();
        int dryersOpen = dryers.getOpen();
        int totalWashers = washers.getOpen() + washers.getRunning() + washers.getOffline() + +washers.getOutOfOrder();
        int totalDryers = dryers.getOpen() + dryers.getRunning() + dryers.getOffline() + dryers.getOutOfOrder();
        holder.roomNameTV.setText(room.getName());
        holder.washerAvailabilityTV.setText("washers | open: " + washersOpen + " | total: " + totalWashers);
        holder.dryerAvailabilityTV.setText("dryers | open: " + dryersOpen + " | total: " + totalDryers);
    }

    @Override
    public int getItemCount() {
        return mLaundryRooms.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.laundry_room_name_home)
        TextView roomNameTV;
        @Bind(R.id.washer_availability_home)
        TextView washerAvailabilityTV;
        @Bind(R.id.dryer_availability_home)
        TextView dryerAvailabilityTV;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}