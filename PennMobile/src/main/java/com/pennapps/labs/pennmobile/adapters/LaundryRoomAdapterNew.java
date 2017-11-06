package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Dryers;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.Washers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomAdapterNew extends RecyclerView.Adapter<LaundryRoomAdapterNew.CustomViewHolder> {

    Context mContext;
    ArrayList<LaundryRoom> mRooms;
    String mMachineType;

    public LaundryRoomAdapterNew(Context context, ArrayList<LaundryRoom> rooms, String machineType) {
        mContext = context;
        mRooms = rooms;
        mMachineType = machineType;
    }

    // update data in this adapter
    public void update(ArrayList<LaundryRoom> newData) {
        mRooms.clear();
        mRooms.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public LaundryRoomAdapterNew.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_preview_item, parent, false);
        return new CustomViewHolder(view, mContext, mRooms);
    }

    @Override
    public void onBindViewHolder(LaundryRoomAdapterNew.CustomViewHolder holder, int position) {

        LaundryRoom room = mRooms.get(position);

        // update name of laundry room
        holder.title.setText("Laundry Room " + (position + 1));
        holder.name.setText(room.name);

        if (mMachineType.equals("washer")) {
            Washers washers = room.getMachines().getWashers();

            holder.machine.setText("washers available");

            // laundry availability
            int open = washers.getOpen();
            int running = washers.getRunning();
            // todo specify offline in text
            int offline = washers.getOffline();
            // todo include out of order?
            holder.availability.setText(open + " out of " + (open + running));
            holder.availability.setTextColor(ContextCompat.getColor(mContext, R.color.teal));

            // create an array of time remaining (length is number of machines)
            int totalMachines = open + running + offline;
            List<Integer> timeRemaining = washers.getTimeRemaining();
            int[] times = new int[totalMachines];
            Iterator iterator = timeRemaining.iterator();
            for (int i = 0; i < timeRemaining.size(); i++) {
                times[i] = (int) iterator.next();
            }

            // recycler view for the time remaining
            LaundryMachineAdapterNew adapter = new LaundryMachineAdapterNew(mContext, times, R.color.teal);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(adapter);

        } else {
            Dryers dryers = room.getMachines().getDryers();

            holder.machine.setText("dryers available");

            // laundry availability
            int open = dryers.getOpen();
            int running = dryers.getRunning();
            int offline = dryers.getOffline();
            holder.availability.setText(open + " out of " + (open + running));
            holder.availability.setTextColor(ContextCompat.getColor(mContext, R.color.star_color_on));

            // create an array of time remaining (length is number of machines)
            int totalMachines = open + running + offline;
            List<Integer> timeRemaining = dryers.getTimeRemaining();
            int[] times = new int[totalMachines];
            Iterator iterator = timeRemaining.iterator();
            for (int i = 0; i < timeRemaining.size(); i++) {
                times[i] = (int) iterator.next();
            }

            // recycler view for the time remaining
            LaundryMachineAdapterNew adapter = new LaundryMachineAdapterNew(mContext, times, R.color.star_color_on);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context mContext;
        ArrayList<LaundryRoom> mRooms;

        TextView title;
        TextView name;
        TextView availability;
        TextView machine;
        RecyclerView recyclerView;

        public CustomViewHolder(View view, Context context, ArrayList<LaundryRoom> rooms) {
            super(view);

            mContext = context;
            mRooms = rooms;

            name = (TextView) view.findViewById(R.id.fav_laundry_room_name);
            availability = (TextView) view.findViewById(R.id.laundry_availability);
            recyclerView = (RecyclerView) view.findViewById(R.id.laundry_machine_list);
            machine = (TextView) view.findViewById(R.id.machine_type);
            title = (TextView) view.findViewById(R.id.laundry_room_title);
        }
    }
}