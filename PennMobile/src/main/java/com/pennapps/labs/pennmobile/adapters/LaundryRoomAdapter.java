package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.LaundryFragment;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.List;

public class LaundryRoomAdapter extends ArrayAdapter<LaundryRoom> {
    private List<LaundryRoom> laundries;
    private final LayoutInflater inflater;
    private Context mContext;

    public LaundryRoomAdapter(Context context, List<LaundryRoom> laundries) {
        super(context, R.layout.laundry_list_item, laundries);
        this.laundries = laundries;
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.laundry_list_item, parent, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.tv_hall_name);
        LaundryRoom l = laundries.get(position);
        textView.setText(l.name);
        TextView washer_tv = (TextView) view.findViewById(R.id.tv_washers);
        RelativeLayout washer_rl = (RelativeLayout) view.findViewById(R.id.rl_washers);
        washer_tv.setVisibility(View.VISIBLE);
        washer_tv.setText(R.string.laundry_washer);
        washer_rl.setVisibility(View.VISIBLE);
        TextView dryer_tv = (TextView) view.findViewById(R.id.tv_dryers);
        RelativeLayout dryer_rl = (RelativeLayout) view.findViewById(R.id.rl_dryers);
        dryer_tv.setVisibility(View.VISIBLE);
        dryer_tv.setText(R.string.laundry_dryer);
        dryer_rl.setVisibility(View.VISIBLE);
        LaundryFragment.setSummary(l.washers_available, l.washers_in_use, position, washer_rl, mContext);
        LaundryFragment.setSummary(l.dryers_available, l.dryers_in_use, position, dryer_rl, mContext);
        return view;
    }
}