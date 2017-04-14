package com.pennapps.labs.pennmobile.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver;
import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by andre on 4/14/2017.
 */
class MachineHolder extends RecyclerView.ViewHolder {

    private MainActivity activity;

    @Bind(R.id.laundry_machine_title) TextView title;
    @Bind(R.id.laundry_machine_description) TextView description;
    @Bind(R.id.laundry_notification_button) Switch notification;

    public MachineHolder(View itemView, MainActivity activity) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.activity = activity;
    }

    private void setSwitchState(final LaundryMachine machine, Switch mSwitch, final LaundryRoom laundryRoom, final MainActivity activity) {
        final Intent intent = new Intent(activity, LaundryBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(activity.getString(R.string.laundry_position), laundryRoom.name.hashCode() + machine.number);
        intent.putExtra(activity.getString(R.string.laundry), laundryRoom);
        intent.putExtra(activity.getString(R.string.laundry_machine_intent), machine);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, laundryRoom.name.hashCode() + machine.number,
                intent, PendingIntent.FLAG_NO_CREATE);
        if (alarmIntent == null) {
            mSwitch.setChecked(false);
        } else {
            mSwitch.setChecked(true);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                if (isChecked) {
                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, laundryRoom.name.hashCode() + machine.number,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + machine.getTimeInMilli(), alarmIntent);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(activity.getString(R.string.laundry_notification_snackbar_on))
                            .append(" ").append(machine.machine_type).append(" ")
                            .append(machine.number);
                    Snackbar snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT);
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(activity.getResources().getColor(R.color.white));
                    snackbar.show();
                } else {
                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, laundryRoom.name.hashCode() + machine.number,
                            intent, PendingIntent.FLAG_NO_CREATE);
                    if (alarmIntent != null) {
                        alarmManager.cancel(alarmIntent);
                        alarmIntent.cancel();
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(activity.getString(R.string.laundry_notification_snackbar_off))
                            .append(" ").append(machine.machine_type).append(" ")
                            .append(machine.number);
                    if (buttonView.getContext() == null) {
                        return;
                    }
                    Snackbar snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT);
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(activity.getResources().getColor(R.color.white));
                    snackbar.show();
                }
            }
        });
    }

    public void bindMachineStatusHolder(LaundryMachine machine, LaundryRoom laundryRoom) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(machine.machine_type).append(" ").append(machine.number);
        title.setText(stringBuilder);
        if (machine.available) {
            description.setText(R.string.laundry_available);
            description.setTextColor(activity.getResources().getColor(R.color.avail_color_green));
            notification.setVisibility(View.GONE);
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Busy – ").append(machine.getTimeLeft(activity));
            description.setText(stringBuilder);
            if (machine.getTimeInMilli() > 0) {
                notification.setVisibility(View.VISIBLE);
            } else {
                notification.setVisibility(View.GONE);
            }
            setSwitchState(machine, notification, laundryRoom, activity);
            description.setTextColor(activity.getResources().getColor(R.color.avail_color_red));
        }
    }
}
