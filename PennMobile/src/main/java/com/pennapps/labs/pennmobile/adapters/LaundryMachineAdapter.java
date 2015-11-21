package com.pennapps.labs.pennmobile.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver;
import com.pennapps.labs.pennmobile.LaundryFragment;
import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Laundry;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Jason on 11/18/2015.
 */
public class LaundryMachineAdapter extends ArrayAdapter<LaundryMachine> {

    private List<LaundryMachine> machines;
    private final LayoutInflater inflater;
    private MainActivity activity;
    private boolean wash;
    private Laundry laundry;

    public LaundryMachineAdapter(MainActivity activity, List<LaundryMachine> machines, boolean wash, Laundry laundry) {
        super(activity, R.layout.laundry_list_item, machines);
        this.machines = machines;
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.wash = wash;
        this.laundry = laundry;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.laundry_machine_list_item, parent, false);
        }
        ButterKnife.bind(this, view);
        LinearLayout summary = (LinearLayout) view.findViewById(R.id.laundry_machine_list_summary);
        RelativeLayout item = (RelativeLayout) view.findViewById(R.id.laundry_machine_list_normal);
        StringBuilder stringBuilder = new StringBuilder();
        if(position == 0){
            item.setVisibility(View.GONE);
            summary.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView) view.findViewById(R.id.laundry_machine_iv);
            RelativeLayout summary_rl = (RelativeLayout) view.findViewById(R.id.laundry_machine_summary_rl);
            TextView description = (TextView) view.findViewById(R.id.laundry_building_description);
            description.setText(laundry.name);
            TextView availcount = (TextView) view.findViewById(R.id.laundry_machine_summary);
            int avail = 0;
            for(LaundryMachine machine: machines){
                if(machine.available){
                    avail++;
                }
            }
            String str = wash? activity.getString(R.string.laundry_washer): activity.getString(R.string.laundry_dryer);
            stringBuilder.append(avail).append(" out of ").append(machines.size()).append(" ")
            .append(str).append(" available");
            availcount.setText(stringBuilder);
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x / 4;
            if(wash){
                Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.washer);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, width, width, true);
                imageView.setImageBitmap(resize);
                LaundryFragment.setSummary(laundry.washers_available, laundry.washers_in_use, 10, summary_rl, activity);
            } else{
                Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dryer);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, width, width, true);
                imageView.setImageBitmap(resize);
                LaundryFragment.setSummary(laundry.dryers_available, laundry.dryers_in_use, 10, summary_rl, activity);
            }
        } else{
            summary.setVisibility(View.GONE);
            item.setVisibility(View.VISIBLE);
            TextView title = (TextView) view.findViewById(R.id.laundry_machine_title);
            final LaundryMachine machine = machines.get(position-1);
            stringBuilder.append(machine.machine_type).append(" ").append(machine.number);
            title.setText(stringBuilder);
            TextView description = (TextView) view.findViewById(R.id.laundry_machine_description);
            Switch mSwitch = (Switch) view.findViewById(R.id.laundry_notification_button);
            if(machine.time_left == null || machine.time_left.equals("null")|| machine.time_left.contains("ago")){
                description.setText(R.string.laundry_available);
                mSwitch.setVisibility(View.GONE);
            } else{
                stringBuilder = new StringBuilder();
                stringBuilder.append("Busy – ").append(machine.time_left);
                description.setText(stringBuilder);
                mSwitch.setVisibility(View.VISIBLE);
                SetSwitchState(machine, mSwitch);
            }
        }
        return view;
    }

    private void SetSwitchState(final LaundryMachine machine, Switch mSwitch) {
        final Intent intent = new Intent(getContext(), LaundryBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(activity.getString(R.string.laundry_hall_no), laundry.hall_no);
        intent.putExtra(activity.getString(R.string.laundry_position), 50 * laundry.hall_no + machine.number);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 50 * laundry.hall_no + machine.number,
                intent, PendingIntent.FLAG_NO_CREATE);
        if(alarmIntent == null) {
            mSwitch.setChecked(false);
        } else{
            mSwitch.setChecked(true);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                final CompoundButton button = buttonView;
                if (isChecked) {
                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 50 * laundry.hall_no + machine.number,
                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, alarmIntent);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(activity.getString(R.string.laundry_notification_snackbar_on))
                            .append(" ").append(machine.machine_type).append(" ")
                            .append(machine.number);
                    Snackbar snackbar = Snackbar.make(buttonView,stringBuilder , Snackbar.LENGTH_SHORT)
                            .setAction(activity.getString(R.string.laundry_undo), new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    button.setChecked(false);
                                    alarmManager.cancel(alarmIntent);
                                    alarmIntent.cancel();
                                }
                            });
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(activity.getResources().getColor(R.color.white));
                    snackbar.show();
                } else {
                    final PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 50 * laundry.hall_no + machine.number,
                            intent, PendingIntent.FLAG_NO_CREATE);
                    if (alarmIntent != null) {
                        alarmManager.cancel(alarmIntent);
                        alarmIntent.cancel();
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(activity.getString(R.string.laundry_notification_snackbar_off))
                            .append(" ").append(machine.machine_type).append(" ")
                            .append(machine.number);
                    Snackbar snackbar = Snackbar.make(buttonView,stringBuilder, Snackbar.LENGTH_SHORT)
                            .setAction(activity.getString(R.string.laundry_undo), new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    button.setChecked(true);
                                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 50 * laundry.hall_no + machine.number,
                                            intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, alarmIntent);
                                }
                            });
                    View subView = snackbar.getView();
                    TextView snackTextView = (TextView) subView.findViewById(android.support.design.R.id.snackbar_text);
                    snackTextView.setTextColor(activity.getResources().getColor(R.color.white));
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public int getCount(){
        return machines.size() + 1;
    }
}
