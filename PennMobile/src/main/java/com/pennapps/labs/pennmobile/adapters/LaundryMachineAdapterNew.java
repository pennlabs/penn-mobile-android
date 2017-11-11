package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import java.util.Arrays;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryMachineAdapterNew extends RecyclerView.Adapter<LaundryMachineAdapterNew.CustomViewHolder> {

    Context mContext;
    int[] mTimes;
    int mColor;

    public LaundryMachineAdapterNew(Context context, int[] times, int color) {
        mContext = context;
        Arrays.sort(times);
        mTimes = times;
        mColor = color;
    }

    @Override
    public LaundryMachineAdapterNew.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        return new CustomViewHolder(view, mContext, mTimes);
    }

    @Override
    public void onBindViewHolder(LaundryMachineAdapterNew.CustomViewHolder holder, int position) {

        int time = mTimes[position];
        String timeString;
        // open
        if (time == LaundryRoomAdapterNew.OPEN_LABEL) {
            timeString = "OPEN";
            holder.button.setBackgroundColor(ContextCompat.getColor(mContext, mColor));
            holder.timeTextView.setText(timeString);
            holder.timeTextView.setTextColor(Color.WHITE);
            holder.textView.setVisibility(View.GONE);
        }
        // not updating status/offline/out of order
        else if (time == LaundryRoomAdapterNew.NOT_UPDATING_STATUS_LABEL || time == LaundryRoomAdapterNew.OFFLINE_LABEL || time == LaundryRoomAdapterNew.OUT_OF_ORDER_LABEL) {
            holder.button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(R.string.not_updating_status);
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText("not available");
        }
        // time left
        else {
            timeString = Integer.toString(time);
            holder.button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(timeString);
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText("min left");
        }
    }

    @Override
    public int getItemCount() {
        return mTimes.length;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        int[] times;
        Button button;
        TextView textView;
        TextView timeTextView;

        public CustomViewHolder(View view, Context context, int[] times) {
            super(view);

            this.context = context;
            this.times = times;
            button = (Button) view.findViewById(R.id.laundry_machine_button);
            textView = (TextView) view.findViewById(R.id.min_left);
            timeTextView = (TextView) view.findViewById(R.id.min_left_time);
        }
    }
}
