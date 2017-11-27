package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.MachineList;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomAdapter extends RecyclerView.Adapter<LaundryRoomAdapter.CustomViewHolder> {

    Context mContext;
    ArrayList<LaundryRoom> mRooms;
    String mMachineType;
    SharedPreferences sp;

    public static final int OPEN_LABEL = 400;
    public static final int NOT_UPDATING_STATUS_LABEL = 401;
    public static final int OFFLINE_LABEL = 402;
    public static final int OUT_OF_ORDER_LABEL = 403;

    public LaundryRoomAdapter(Context context, ArrayList<LaundryRoom> rooms, String machineType) {
        mContext = context;
        mMachineType = machineType;
        mRooms = rooms;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public LaundryRoomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_room_item, parent, false);
        return new CustomViewHolder(view, mContext, mRooms);
    }

    @Override
    public void onBindViewHolder(LaundryRoomAdapter.CustomViewHolder holder, int position) {

        LaundryRoom room = mRooms.get(position);

        // update name of laundry room and type of machine
        int hall_no = room.getId();
        String location = sp.getString(hall_no + mContext.getString(R.string.location), "");
        String roomName = room.getName();
        holder.title.setText(roomName);
        holder.name.setText(location);
        holder.machine.setText(mMachineType + mContext.getString(R.string.s) + " " + mContext.getString(R.string.available));

        MachineList machines;

        // if washer
        if (mMachineType.equals(mContext.getString(R.string.washer))) {
            machines = room.getMachines().getWashers();
        }
        // if dryer
        else {
            machines = room.getMachines().getDryers();
        }

        // laundry availability
        int open = machines.getOpen();
        int running = machines.getRunning();
        int offline = machines.getOffline();
        int outOfOrder = machines.getOutOfOrder();
        int totalMachines = open + running + offline + outOfOrder;
        holder.availability.setText(open + " " + mContext.getString(R.string.out_of) + " " + totalMachines);

        // create an array of time remaining/availability (length is number of machines)
        List<Integer> timeRemaining = machines.getTimeRemaining();

        // add the numbers so they are sorted corrected
        for (int i = 0; i < offline; i++) {
            timeRemaining.add(OFFLINE_LABEL);
        }
        for (int i = 0; i < outOfOrder; i++) {
            timeRemaining.add(OUT_OF_ORDER_LABEL);
        }
        for (int i = 0; i < open; i++) {
            timeRemaining.add(OPEN_LABEL);
        }

        int[] times = new int[totalMachines];

        // change -1 to not_updating_status_label so when sorted it will be at the end
        for (int i = 0; i < timeRemaining.size(); i++) {
            int time = timeRemaining.get(i);
            // not updating status
            if (time == -1) {
                times[i] = NOT_UPDATING_STATUS_LABEL;
            } else {
                times[i] = time;
            }
        }

        // if washer
        if (mMachineType.equals(mContext.getString(R.string.washer))) {
            // recycler view for the time remaining
            holder.availability.setTextColor(ContextCompat.getColor(mContext, R.color.teal));
            LaundryMachineAdapter adapter = new LaundryMachineAdapter(mContext, times, mMachineType, roomName);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(adapter);
        }
        // if dryer
        else {
            // recycler view for the time remaining
            holder.availability.setTextColor(ContextCompat.getColor(mContext, R.color.star_color_on));
            LaundryMachineAdapter adapter = new LaundryMachineAdapter(mContext, times, mMachineType, roomName);
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

        @Bind(R.id.laundry_room_title)
        TextView title;
        @Bind(R.id.fav_laundry_room_name)
        TextView name;
        @Bind(R.id.laundry_availability)
        TextView availability;
        @Bind(R.id.machine_type)
        TextView machine;
        @Bind(R.id.laundry_machine_list)
        RecyclerView recyclerView;

        public CustomViewHolder(View view, Context context, ArrayList<LaundryRoom> rooms) {
            super(view);

            mContext = context;
            mRooms = rooms;

            ButterKnife.bind(this, view);
        }
    }
}