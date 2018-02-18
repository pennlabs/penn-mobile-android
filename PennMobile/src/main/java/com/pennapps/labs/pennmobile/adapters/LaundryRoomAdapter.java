package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.MachineDetail;
import com.pennapps.labs.pennmobile.classes.MachineList;
import com.pennapps.labs.pennmobile.classes.Machines;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomAdapter extends RecyclerView.Adapter<LaundryRoomAdapter.CustomViewHolder> {

    Context mContext;
    ArrayList<LaundryRoom> mRooms;
    List<LaundryUsage> mRoomsData;
    String mMachineType;
    SharedPreferences sp;

    public LaundryRoomAdapter(Context context, ArrayList<LaundryRoom> rooms, String machineType, List<LaundryUsage> roomsData) {
        mContext = context;
        mRooms = rooms;
        mRoomsData = roomsData;
        mMachineType = machineType;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public LaundryRoomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_room_item, parent, false);
        return new CustomViewHolder(view, mContext, mRooms, mRoomsData);
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

        Machines machines = room.getMachines();
        List<MachineDetail> machineDetails = machines.getMachineDetailList();

        List<MachineDetail> washers = new ArrayList<>();
        List<MachineDetail> dryers = new ArrayList<>();

        for (MachineDetail machineDetail : machineDetails) {
            if (machineDetail.getType().equals("washer")) {
                washers.add(machineDetail);
            } else {
                dryers.add(machineDetail);
            }
        }

        // if washer
        if (mMachineType.equals(mContext.getString(R.string.washer))) {
            // recycler view for the time remaining
            LaundryMachineAdapter adapter = new LaundryMachineAdapter(mContext, washers, mMachineType, roomName);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(adapter);
        }
        // if dryer
        else {
            // recycler view for the time remaining
            LaundryMachineAdapter adapter = new LaundryMachineAdapter(mContext, dryers, mMachineType, roomName);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(adapter);
        }

        // overview of how many machines are availabe
        MachineList machineList;

        // if washer
        if (mMachineType.equals(mContext.getString(R.string.washer))) {
            machineList = room.getMachines().getWashers();
        }
        // if dryer
        else {
            machineList = room.getMachines().getDryers();
        }

        // laundry availability
        int open = machineList.getOpen();
        int running = machineList.getRunning();
        int offline = machineList.getOffline();
        int outOfOrder = machineList.getOutOfOrder();
        int totalMachines = open + running + offline + outOfOrder;
        holder.availability.setText(open + " " + mContext.getString(R.string.out_of) + " " + totalMachines);

        LaundryUsage roomUsage = mRoomsData.get(position);

        List<Double> roomData;
        // if washer
        if (mMachineType.equals(mContext.getString(R.string.washer))) {
            roomData = roomUsage.getWasherData().getAdjustedData();
        }
        // if dryer
        else {
            roomData = roomUsage.getDryerData().getAdjustedData();
        }

        Double[] roomDataArr = new Double[roomData.size()];
        roomDataArr = roomData.toArray(roomDataArr);

        List<Entry> graphEntries = new ArrayList<>();

        for (int i = 0; i < roomDataArr.length; i++) {
            graphEntries.add(new Entry(i, roomDataArr[i].floatValue()));
        }

        // add entries to dataset
        LineDataSet dataSet = new LineDataSet(graphEntries, "Traffic");
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);

        // curvy line
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(dataSet);

        // set data to chart
        LineChart laundryChart = holder.lineChart;
        laundryChart.setData(lineData);

        // styling of chart
        laundryChart.setTouchEnabled(false);
        laundryChart.getXAxis().setDrawGridLines(false);
        laundryChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        laundryChart.getXAxis().setValueFormatter(new TimeXAxisValueFormatter());
        laundryChart.getAxisRight().setEnabled(false);
        laundryChart.getAxisLeft().setEnabled(false);
        laundryChart.setDrawBorders(false);
        laundryChart.setDescription(null);
        int hourOfDay = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);

        // highlight time of day
        LimitLine ll = new LimitLine(hourOfDay);
        laundryChart.getXAxis().addLimitLine(ll);

        laundryChart.getAxisLeft().setAxisMinimum(0);
        int maxIndex = roomData.indexOf(Collections.max(roomData));
        laundryChart.getAxisLeft().setAxisMaximum(roomData.get(maxIndex).floatValue() + roomData.get(maxIndex).floatValue() / 10);
        laundryChart.fitScreen();
        Legend legend = laundryChart.getLegend();
        legend.setEnabled(false);
        laundryChart.invalidate();
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
        @Bind(R.id.laundry_availability_chart)
        LineChart lineChart;

        public CustomViewHolder(View view, Context context, ArrayList<LaundryRoom> rooms, List<LaundryUsage> roomsData) {
            super(view);

            mContext = context;
            mRooms = rooms;

            ButterKnife.bind(this, view);
        }
    }
}