package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryHall;

import java.util.List;

/**
 * Created by Jason on 10/21/2015.
 */
public class LaundryHallAdapter extends ArrayAdapter<LaundryHall> {
    private List<LaundryHall> laundries;
    private final LayoutInflater inflater;
    private Context mContext;

    public LaundryHallAdapter(Context context, List<LaundryHall> laundries) {
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
        LaundryHall lh = laundries.get(position);
        textView.setText(lh.getName());
        return view;
    }
}
