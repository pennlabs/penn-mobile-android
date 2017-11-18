package com.pennapps.labs.pennmobile.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.LaundryBroadcastReceiverNew;
import com.pennapps.labs.pennmobile.R;

import java.util.Arrays;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryMachineAdapterNew extends RecyclerView.Adapter<LaundryMachineAdapterNew.CustomViewHolder> {

    Context mContext;
    int[] mTimes;
    int mColor;
    String mRoomName;
    String mMachineType;

    public LaundryMachineAdapterNew(Context context, int[] times, String machineType, String roomName) {
        mContext = context;
        Arrays.sort(times);
        mTimes = times;
        mMachineType = machineType;
        mRoomName = roomName;
        if (machineType.equals("washer")) {
            mColor = R.color.teal;
        } else {
            mColor = R.color.star_color_on;
        }
    }

    @Override
    public LaundryMachineAdapterNew.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        return new CustomViewHolder(view, mContext, mTimes);
    }

    @Override
    public void onBindViewHolder(LaundryMachineAdapterNew.CustomViewHolder holder, int position) {

        int time = mTimes[position];

        // open
        if (time == LaundryRoomAdapterNew.OPEN_LABEL) {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, mColor));
            holder.timeTextView.setText("OPEN");
            holder.timeTextView.setTextColor(Color.WHITE);
            holder.textView.setVisibility(View.GONE);
            //holder.alarmSwitch.setVisibility(View.GONE);
        }
        // not updating status/offline/out of order
        else if (time == LaundryRoomAdapterNew.NOT_UPDATING_STATUS_LABEL || time == LaundryRoomAdapterNew.OFFLINE_LABEL || time == LaundryRoomAdapterNew.OUT_OF_ORDER_LABEL) {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(R.string.not_updating_status);
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText("not available");
            //holder.alarmSwitch.setVisibility(View.GONE);
        }
        // time left
        else {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(Integer.toString(time));
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText("min left");
            // switch to turn on alarm
            //holder.alarmSwitch.setVisibility(View.VISIBLE);
            //setSwitchState(time, holder.alarmSwitch, position);
        }
    }

    @Override
    public int getItemCount() {
        return mTimes.length;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        int[] times;
        View machineView;
        TextView textView;
        TextView timeTextView;
        //Switch alarmSwitch;

        public CustomViewHolder(View view, Context context, int[] times) {
            super(view);

            this.context = context;
            this.times = times;
            machineView = view.findViewById(R.id.laundry_machine_view);
            textView = (TextView) view.findViewById(R.id.min_left);
            timeTextView = (TextView) view.findViewById(R.id.min_left_time);
            //alarmSwitch = (Switch) view.findViewById(R.id.laundry_alarm_switch);
        }
    }

    // todo creates alarm for laundry
    private void setSwitchState(final int time, Switch mSwitch, final int machineId) {

        final int id = (mRoomName + mMachineType).hashCode() + machineId;

        final Intent intent = new Intent(mContext, LaundryBroadcastReceiverNew.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("roomName", mRoomName);
        intent.putExtra("machineType", mMachineType);
        intent.putExtra("id", id);

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
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time * 60000, alarmIntent);

                    // snackbar
                    StringBuilder stringBuilder = new StringBuilder();
                    //stringBuilder.append("alarm on, " + "id: " + machineId + ", time: " + time + ", name: " + mRoomName);
                    stringBuilder.append("time: " + time);
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
                    stringBuilder.append("alarm off");
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
