package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
        TextView hallStatus = (TextView) view.findViewById(R.id.dining_hall_status);
        TextView breakfastMenuTV = (TextView) view.findViewById(R.id.dining_hall_breakfast);
        TextView brunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_brunch);
        TextView lunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_lunch);
        TextView dinnerMenuTV = (TextView) view.findViewById(R.id.dining_hall_dinner);

        hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));
        if (diningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(getContext().getResources().getDrawable(R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(getContext().getResources().getDrawable(R.drawable.label_red));
        }

        for (Map.Entry<String, HashMap<String, String>> menu : diningHall.menus.entrySet()) {
            String mealName = StringUtils.capitalize(menu.getKey());
            String menuText = "";
            for (Map.Entry<String, String> menuItem : menu.getValue().entrySet()) {
                String key = menuItem.getKey();
                String value = menuItem.getValue();
                menuText += key + ": " + value + "\n";
            }
            if (mealName.equals("Breakfast")) {
                view.findViewById(R.id.dining_hall_breakfast_title).setVisibility(View.VISIBLE);
                breakfastMenuTV.setVisibility(View.VISIBLE);
                breakfastMenuTV.setText(menuText);
            } else if (mealName.equals("Brunch")) {
                view.findViewById(R.id.dining_hall_brunch_title).setVisibility(View.VISIBLE);
                brunchMenuTV.setVisibility(View.VISIBLE);
                brunchMenuTV.setText(menuText);
            } else if (mealName.equals("Lunch")) {
                view.findViewById(R.id.dining_hall_lunch_title).setVisibility(View.VISIBLE);
                lunchMenuTV.setVisibility(View.VISIBLE);
                lunchMenuTV.setText(menuText);
            } else if (mealName.equals("Dinner")) {
                view.findViewById(R.id.dining_hall_dinner_title).setVisibility(View.VISIBLE);
                dinnerMenuTV.setVisibility(View.VISIBLE);
                dinnerMenuTV.setText(menuText);
            }
        }
        this.sort(new MenuComparator());
        return view;
    }

    private class MenuComparator implements Comparator<DiningHall> {
        @Override
        public int compare(DiningHall diningHall, DiningHall diningHall2) {
            if (diningHall.isResidential() && !diningHall2.isResidential()) {
                return -1;
            } else if (diningHall2.isResidential() && !diningHall.isResidential()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
