package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pennapps.labs.pennmobile.FlingPerformanceViewHolder;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

public class FlingRecyclerViewAdapter extends RecyclerView.Adapter<FlingPerformanceViewHolder> {

    Context context;
    List<FlingEvent> data;
    DateTimeFormatter timeFormatter;

    public FlingRecyclerViewAdapter(Context context, List<FlingEvent> sampleData) {
        this.context = context;
        data = sampleData;
        timeFormatter = ISODateTimeFormat.dateTimeNoMillis();
    }

    @Override
    public FlingPerformanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fling_performance_item, parent, false);
        return new FlingPerformanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlingPerformanceViewHolder holder, int position) {
        if (position < getItemCount()) {
            FlingEvent flingEvent = data.get(position);
            Picasso.get().load(flingEvent.imageUrl).into(holder.flingview_image);
            holder.flingview_name.setText(flingEvent.name);
            holder.flingview_description.setText(flingEvent.description);
            DateTime startTime = timeFormatter.parseDateTime(flingEvent.startTime);
            DateTime endTime = timeFormatter.parseDateTime(flingEvent.endTime);
            DateTimeFormatter dtfStart = DateTimeFormat.forPattern("h:mm");
            DateTimeFormatter dtfEnd = DateTimeFormat.forPattern("h:mm a");
            holder.flingview_time.setText(String.format(context.getResources().getString(R.string.fling_event_time), dtfStart.print(startTime), dtfEnd.print(endTime)));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
