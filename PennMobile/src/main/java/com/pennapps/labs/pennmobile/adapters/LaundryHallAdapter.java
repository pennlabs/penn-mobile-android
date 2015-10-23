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

    public LaundryHallAdapter(Context context, List<LaundryHall> laundries) {
        super(context, android.R.layout.simple_list_item_1, laundries);
        this.laundries = laundries;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(laundries.get(position).getName());
        return view;
    }
}
