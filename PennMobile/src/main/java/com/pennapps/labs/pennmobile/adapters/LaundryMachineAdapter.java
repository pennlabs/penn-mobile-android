package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2017-10-22.
 */

public class LaundryMachineAdapter extends RecyclerView.Adapter<LaundryMachineAdapter.CustomViewHolder> {

    Context mContext;
    int[] mTimes;
    int mColor;
    String mRoomName;
    String mMachineType;

    public LaundryMachineAdapter(Context context, int[] times, String machineType, String roomName) {
        mContext = context;
        Arrays.sort(times);
        mTimes = times;
        mMachineType = machineType;
        mRoomName = roomName;
        if (machineType.equals(mContext.getString(R.string.washer))) {
            mColor = R.color.teal;
        } else {
            mColor = R.color.star_color_on;
        }
    }

    @Override
    public LaundryMachineAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_machine_item, parent, false);
        return new CustomViewHolder(view, mContext, mTimes);
    }

    @Override
    public void onBindViewHolder(LaundryMachineAdapter.CustomViewHolder holder, int position) {

        int time = mTimes[position];

        // open
        if (time == LaundryRoomAdapter.OPEN_LABEL) {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, mColor));
            holder.timeTextView.setText(R.string.open);
            holder.timeTextView.setTextColor(Color.WHITE);
            holder.textView.setVisibility(View.GONE);
        }
        // not updating status/offline/out of order
        else if (time == LaundryRoomAdapter.NOT_UPDATING_STATUS_LABEL || time == LaundryRoomAdapter.OFFLINE_LABEL || time == LaundryRoomAdapter.OUT_OF_ORDER_LABEL) {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(R.string.not_updating_status);
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(R.string.not_available);
        }
        // time left
        else {
            holder.machineView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.star_color_off));
            holder.timeTextView.setText(Integer.toString(time));
            holder.timeTextView.setTextColor(ContextCompat.getColor(mContext, mColor));
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(R.string.min_left);
        }
    }

    @Override
    public int getItemCount() {
        return mTimes.length;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        int[] times;
        @Bind(R.id.laundry_machine_view)
        View machineView;
        @Bind(R.id.min_left)
        TextView textView;
        @Bind(R.id.min_left_time)
        TextView timeTextView;

        public CustomViewHolder(View view, Context context, int[] times) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            this.times = times;
        }
    }
}
