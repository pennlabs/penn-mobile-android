package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class DiningAdapter extends ArrayAdapter<DiningHall> {

    public DiningAdapter(Context context, ArrayList<DiningHall> diningHalls) {
        super(context, R.layout.dining_list_item, diningHalls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiningHall diningHall = getItem(position);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dining_list_item, null);

        TextView hallNameTV = (TextView) view.findViewById(R.id.dining_hall_name);
        TextView dinnerMenuTV = (TextView) view.findViewById(R.id.dining_hall_dinner);
        TextView lunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_lunch);

        String open = diningHall.isOpen() ? "Open" : "Closed";
        hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName() + ": " + open));
        dinnerMenuTV.setText("DINNER");
        lunchMenuTV.setText("LUNCH");

        if (diningHall.getDinnerMenu() != null) {
            String dinnerText = "";
            for (String key : diningHall.getDinnerMenu().keySet()) {
                dinnerText += key + ": " + diningHall.getDinnerMenu().get(key) + "\n";
            }
            dinnerMenuTV.setText(dinnerText);
        }

        return view;
    }

}
