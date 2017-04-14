package com.pennapps.labs.pennmobile.adapters;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.pennapps.labs.pennmobile.LaundryFragment;
import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by andre on 4/14/2017.
 */
class SummaryHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.laundry_machine_iv) ImageView imageView;
    @Bind(R.id.laundry_machine_summary_rl) RelativeLayout summary_rl;
    @Bind(R.id.laundry_building_description) TextView description;
    @Bind(R.id.laundry_machine_summary) TextView availcount;
    @Bind(R.id.laundry_chart) BarChart laundryChart;

    public SummaryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindSummary(MainActivity activity, LaundryRoom laundryRoom, List<LaundryMachine> machines, boolean wash, int[] traffic) {
        description.setText(laundryRoom.name);
        int avail = 0;
        for (LaundryMachine machine : machines) {
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
