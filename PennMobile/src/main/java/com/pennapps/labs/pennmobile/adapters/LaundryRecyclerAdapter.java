package com.pennapps.labs.pennmobile.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver;
import com.pennapps.labs.pennmobile.LaundryFragment;
import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew on 3/24/2017.
 */

public class LaundryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class SummaryHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RelativeLayout summary_rl;
        private TextView description;
        private TextView availcount;
        private BarChart laundryChart;

        public SummaryHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.laundry_machine_iv);
            summary_rl = (RelativeLayout) itemView.findViewById(R.id.laundry_machine_summary_rl);
            description = (TextView) itemView.findViewById(R.id.laundry_building_description);
            availcount = (TextView) itemView.findViewById(R.id.laundry_machine_summary);
            laundryChart = (BarChart) itemView.findViewById(R.id.laundry_chart);
        }

        public void bindSummary(MainActivity activity, LaundryRoom laundryRoom, List<LaundryMachine> machines, boolean wash, int[] traffic) {
            description.setText(laundryRoom.name);
            int avail = 0;
            for (LaundryMachine machine: machines) {
                if (machine.available) {
                    avail++;
                }
            }
            String str = wash ? activity.getString(R.string.laundry_washer) : activity.getString(R.string.laundry_dryer);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(avail).append(" out of ").append(machines.size()).append(" ")
                    .append(str).append(" available");
            availcount.setText(stringBuilder);
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x / 4;
            List<BarEntry> dataEntries = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                dataEntries.add(new BarEntry(i, traffic[i]));
            }
            int timeOfDay = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
            BarDataSet barDataSet = new BarDataSet(dataEntries, "Traffic");
            barDataSet.setDrawValues(false);
            laundryChart.setTouchEnabled(false);
            laundryChart.getXAxis().setDrawGridLines(false);
            laundryChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            laundryChart.getXAxis().setValueFormatter(new TimeXAxisValueFormatter());
            laundryChart.getAxisRight().setEnabled(false);
            laundryChart.getAxisLeft().setEnabled(false);
            laundryChart.setDrawBorders(false);
            laundryChart.setDescription(null);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.5f);
            laundryChart.setData(barData);
            laundryChart.highlightValue(timeOfDay, 0);
            laundryChart.getAxisLeft().setAxisMinimum(0);
            laundryChart.getAxisLeft().setAxisMaximum(6);
            laundryChart.fitScreen();
            laundryChart.animateXY(500, 500);
            laundryChart.invalidate();
            if (wash) {
                Picasso.with(activity).load(R.drawable.washer).resize(width, width).into(imageView);
                LaundryFragment.setSummary(laundryRoom.washers_available, laundryRoom.washers_in_use, 10, summary_rl, activity);
            } else {
                Picasso.with(activity).load(R.drawable.dryer).resize(width, width).into(imageView);
                LaundryFragment.setSummary(laundryRoom.dryers_available, laundryRoom.dryers_in_use, 10, summary_rl, activity);
            }
        }
    }

    static class MachineHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private Switch notification;
        private MainActivity activity;

        public MachineHolder(View itemView, MainActivity activity) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.laundry_machine_title);
            description = (TextView) itemView.findViewById(R.id.laundry_machine_description);
            notification = (Switch) itemView.findViewById(R.id.laundry_notification_button);
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
                        Snackbar snackbar = Snackbar.make(buttonView,stringBuilder , Snackbar.LENGTH_SHORT);
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
                        Snackbar snackbar = Snackbar.make(buttonView,stringBuilder, Snackbar.LENGTH_SHORT);
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

    private List<LaundryMachine> machines;
    private MainActivity activity;
    private boolean wash;
    private LaundryRoom laundryRoom;
    private int[] traffic;

    private static class TimeXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int time = (int) (value % 12);
            if (value > 12) {
                return time + "p";
            }
            if (value >= 12 && time == 0) {
                return "12p";
            }
            if (time == 0) {
                return "12a";
            }
            return time + "a";
        }
    }

    public LaundryRecyclerAdapter(MainActivity activity, LaundryRoom laundryRoom) {
        super();
        this.machines = new LinkedList<>();
        this.activity = activity;
        wash = false;
        this.laundryRoom = laundryRoom;
        traffic = new int[24];
    }

    public LaundryRecyclerAdapter(MainActivity activity, List<LaundryMachine> machines, boolean wash, int[] laundryTraffic, LaundryRoom laundryRoom) {
        super();
        this.machines = machines;
        this.activity = activity;
        this.wash = wash;
        this.laundryRoom = laundryRoom;
        traffic = laundryTraffic;
        smoothTrafficData();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void smoothTrafficData() {
        int[] copyTraffic = new int[traffic.length];
        for (int i = 0; i < copyTraffic.length; i++) {
            copyTraffic[i] = traffic[i];
        }
        for (int i = 1; i < traffic.length - 1; i++) {
            if (Math.abs(copyTraffic[i] - (copyTraffic[i - 1] + copyTraffic[i + 1]) / 2) <=
                    Math.abs(copyTraffic[i - 1] - copyTraffic[i + 1]) / 2) {
                traffic[i] = (copyTraffic[i - 1] + copyTraffic[i + 1]) / 2;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        switch (viewType) {
            case 0:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_summary_item, parent, false);
                return new SummaryHolder(inflatedView);
            case 1:
                inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
                return new MachineHolder(inflatedView, activity);
        }
        inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        return new MachineHolder(inflatedView, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                SummaryHolder summaryHolder = (SummaryHolder) holder;
                summaryHolder.bindSummary(activity, laundryRoom, machines, wash, traffic);
                break;
            case 1:
                if (position <= machines.size()) {
                    LaundryMachine ms = machines.get(position - 1);
                    MachineHolder machineHolder = (MachineHolder) holder;
                    machineHolder.bindMachineStatusHolder(ms, laundryRoom);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return machines.size() + 1;
    }
}
