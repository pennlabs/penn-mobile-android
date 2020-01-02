package com.pennapps.labs.pennmobile;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlingPerformanceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.flingview_image)
    public ImageView flingview_image;
    @BindView(R.id.flingview_name)
    public TextView flingview_name;
    @BindView(R.id.flingview_description)
    public TextView flingview_description;
    @BindView(R.id.flingview_time)
    public TextView flingview_time;

    public FlingPerformanceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
