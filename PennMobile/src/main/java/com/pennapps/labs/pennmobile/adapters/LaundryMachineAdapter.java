package com.pennapps.labs.pennmobile.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.LaundryActivity;
import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.MachineDetail;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryMachineAdapter extends RecyclerView.Adapter<LaundryMachineAdapter.CustomViewHolder> {

    Context mContext;
    List<MachineDetail> mMachineDetails;
    String mRoomName;
    String mMachineType;

    // labels for ordering
    public static final int OPEN_LABEL = 400;
    public static final int NOT_AVAILABLE_LABEL = 404;

    public LaundryMachineAdapter(Context context, List<MachineDetail> machinesDetails, String machineType, String roomName) {
        mContext = context;
        mMachineDetails = machinesDetails;

        // sort time remaining so that in use goes first, then open, then not available
        for (MachineDetail detail : mMachineDetails) {
            int timeRemaining = detail.getTimeRemaining();
            String status = detail.getStatus();

            if (timeRemaining == 0) {
                // open
                detail.setTimeRemaining(OPEN_LABEL);
            }
            if (status.equals(mContext.getResources().getString(R.string.status_out_of_order))) {
                // not available
                detail.setTimeRemaining(NOT_AVAILABLE_LABEL);
            }
        }
        Collections.sort(mMachineDetails);

        mMachineType = machineType;
        mRoomName = roomName;
    }

    @Override
    public LaundryMachineAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (mMachineType.equals(mContext.getResources().getString(R.string.washer))) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_dryer_item, parent, false);
        }

        return new CustomViewHolder(view, mContext, mMachineDetails);
    }

    @Override
    public void onBindViewHolder(LaundryMachineAdapter.CustomViewHolder holder, int position) {

        MachineDetail detail = mMachineDetails.get(position);
        int timeRemaining = detail.getTimeRemaining();

        // not available
        if (timeRemaining == NOT_AVAILABLE_LABEL) {
            if (mMachineType.equals(mContext.getString(R.string.washer))) {
                holder.machineView.setImageResource(R.drawable.washer_na);
            } else {
                holder.machineView.setImageResource(R.drawable.dryer_na);
            }
            holder.timeTextView.setText(R.string.not_updating_status);
            holder.alarmSwitch.setVisibility(View.GONE);
        }

        // if open
        else if (timeRemaining == OPEN_LABEL) {
            if (mMachineType.equals(mContext.getString(R.string.washer))) {
                holder.machineView.setImageResource(R.drawable.washer_available);
            } else {
                holder.machineView.setImageResource(R.drawable.dryer_available);
            }
            holder.timeTextView.setText(R.string.open);
            holder.alarmSwitch.setVisibility(View.GONE);
        }

        // time remaining
        else {
            if (mMachineType.equals(mContext.getString(R.string.washer))) {
                holder.machineView.setImageResource(R.drawable.washer_in_use);
                holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.washer_blue));
            } else {
                holder.machineView.setImageResource(R.drawable.dryer_in_use);
                holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.dryer_red));
            }
            holder.timeTextView.setText(Integer.toString(timeRemaining));
            holder.alarmSwitch.setVisibility(View.VISIBLE);
            int time = detail.getTimeRemaining();
            int id = detail.getId();
            setSwitchState(time, holder.alarmSwitch, id);
        }
    }

    @Override
    public int getItemCount() {
        return mMachineDetails.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        List<MachineDetail> machineDetails;
        @BindView(R.id.laundry_machine_image_view)
        ImageView machineView;
        @BindView(R.id.min_left_time)
        TextView timeTextView;
        Switch alarmSwitch;

        public CustomViewHolder(View view, Context context, List<MachineDetail> machineDetails) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            this.machineDetails = machineDetails;
            alarmSwitch = (Switch) view.findViewById(R.id.laundry_alarm_switch);
        }
    }

    // adds alarm to machine
    private void setSwitchState(final int time, Switch mSwitch, final int machineId) {

        final int id = (mRoomName + mMachineType).hashCode() + machineId;

        final Intent intent = new Intent(mContext, LaundryBroadcastReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(mContext.getResources().getString(R.string.laundry_room_name), mRoomName);
        intent.putExtra(mContext.getResources().getString(R.string.laundry_machine_type), mMachineType);
        intent.putExtra(mContext.getResources().getString(R.string.laundry_machine_id), id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_NO_CREATE);

        // switch is off if no alarm
        if (alarmIntent == null) {
            mSwitch.setChecked(false);
        }
        // switch is on if alarm exists
        else {
            mSwitch.setChecked(true);
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

                // checked button
                if (isChecked) {

                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // for testing 10 second notification
                    // alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, alarmIntent);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time * 60000, alarmIntent);

                    // snackbar
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Alarm set for " + time + " minutes");
                    Snackbar snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT);
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    snackbar.show();
                }

                // unchecked button
                else {
                    // cancel alarm if exists
                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_NO_CREATE);
                    if (alarmIntent != null) {
                        alarmManager.cancel(alarmIntent);
                        alarmIntent.cancel();
                    }

                    if (buttonView.getContext() == null) {
                        return;
                    }

                    // snackbar
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Alarm off");
                    Snackbar snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT);
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    snackbar.show();
                }
            }
        });
    }
}
