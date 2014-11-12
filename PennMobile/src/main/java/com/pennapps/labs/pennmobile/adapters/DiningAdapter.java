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
        TextView breakfastMenuTV = (TextView) view.findViewById(R.id.dining_hall_breakfast);
        TextView brunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_brunch);
        TextView lunchMenuTV = (TextView) view.findViewById(R.id.dining_hall_lunch);
        TextView dinnerMenuTV = (TextView) view.findViewById(R.id.dining_hall_dinner);

        hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));
        dinnerMenuTV.setText("DINNER");
        lunchMenuTV.setText("LUNCH");

        for (Map.Entry<String, HashMap<String, String>> menu : diningHall.menus.entrySet()) {
            String mealName = StringUtils.capitalize(menu.getKey());
            String menuText = "";
            for (Map.Entry<String, String> menuItem : menu.getValue().entrySet()) {
                String key = menuItem.getKey();
                String value = menuItem.getValue();
                menuText += key + ": " + value + "\n";
            }
            if (mealName.equals("Breakfast")) {
                view.findViewById(R.id.dining_hall_breakfast_title).setVisibility(view.VISIBLE);
                breakfastMenuTV.setVisibility(View.VISIBLE);
                breakfastMenuTV.setText(menuText);
            } else if (mealName.equals("Brunch")) {
                view.findViewById(R.id.dining_hall_brunch_title).setVisibility(view.VISIBLE);
                brunchMenuTV.setVisibility(View.VISIBLE);
                brunchMenuTV.setText(menuText);
            } else if (mealName.equals("Lunch")) {
                view.findViewById(R.id.dining_hall_lunch_title).setVisibility(view.VISIBLE);
                lunchMenuTV.setVisibility(View.VISIBLE);
                lunchMenuTV.setText(menuText);
            } else if (mealName.equals("Dinner")) {
                view.findViewById(R.id.dining_hall_dinner_title).setVisibility(view.VISIBLE);
                dinnerMenuTV.setVisibility(View.VISIBLE);
                dinnerMenuTV.setText(menuText);
            }
        }

        return view;
    }

}
