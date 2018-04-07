package com.pennapps.labs.pennmobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FlingPerformanceViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.flingview_image)
    public ImageView flingview_image;
    @Bind(R.id.flingview_name)
    public TextView flingview_name;
    @Bind(R.id.flingview_description)
    public TextView flingview_description;
    @Bind(R.id.flingview_time)
    public TextView flingview_time;

    public FlingPerformanceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
