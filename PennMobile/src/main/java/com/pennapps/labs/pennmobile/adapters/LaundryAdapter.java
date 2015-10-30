package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Laundry;
import com.pennapps.labs.pennmobile.classes.LaundryHall;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jason on 10/30/2015.
 */
public class LaundryAdapter extends ArrayAdapter<Laundry> {
    private List<Laundry> laundries;
    private final LayoutInflater inflater;
    private Context mContext;

    public LaundryAdapter(Context context, List<Laundry> laundries) {
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
        Laundry l = laundries.get(position);
        textView.setText(l.name);
        TextView washer_tv = (TextView) view.findViewById(R.id.tv_washers);
        RelativeLayout washer_rl = (RelativeLayout) view.findViewById(R.id.rl_washers);
        washer_tv.setVisibility(View.VISIBLE);
        washer_tv.setText(R.string.laundry_washer);
        washer_rl.setVisibility(View.VISIBLE);
        LinkedList<ImageView> vertical_washers = new LinkedList<>();
        ImageView prev = null;
        for (int i = 0; i < l.washers_available + l.washers_in_use; i++) {
            RelativeLayout.LayoutParams layparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i < 8) {
                layparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else if (i == 8){
                layparam.addRule(RelativeLayout.BELOW, vertical_washers.getLast().getId());
            } else {
                layparam.addRule(RelativeLayout.BELOW, vertical_washers
                        .get(vertical_washers.size() - 2).getId());
            }
            if(i % 8 == 0){
                layparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else if(prev != null){
                layparam.addRule(RelativeLayout.RIGHT_OF, prev.getId());
            }
            layparam.setMargins(0, 0, 5, 0);
            ImageView imageView = new ImageView(mContext);
            if(i < l.washers_available){
                imageView.setImageResource(R.drawable.green_circle);
            } else {
                imageView.setImageResource(R.drawable.red_circle);
            }
            imageView.setId(position *300 + i);
            washer_rl.addView(imageView, layparam);
            if(i % 8 == 0){
                vertical_washers.add(imageView);
            }
            prev = imageView;
        }
        return view;
    }
}
