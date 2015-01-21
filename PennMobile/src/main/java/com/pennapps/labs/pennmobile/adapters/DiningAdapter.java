package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;

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
        TextView openMeal = (TextView) view.findViewById(R.id.dining_hall_open_meal);
        TextView openClose = (TextView) view.findViewById(R.id.dining_hall_open_close);
        ImageView menuArrow = (ImageView) view.findViewById(R.id.dining_hall_menu_indicator);
        menuArrow.setVisibility(View.GONE);
        view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.VISIBLE);
        view.findViewById(R.id.dining_hall_open_close).setVisibility(View.VISIBLE);

        hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));
        view.setTag(diningHall);

        if (diningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(getContext().getResources().getDrawable(R.drawable.label_green));
            if (!diningHall.openMeal().equals("all")) {
                openMeal.setText("Currently serving " + diningHall.openMeal());
            } else {
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            }
            openClose.setText("Closes at " + diningHall.closingTime());
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(getContext().getResources().getDrawable(R.drawable.label_red));
            String meal = diningHall.nextMeal();
            if (meal.equals("") || meal.equals("all")) {
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            } else {
                openMeal.setText("Next serving " + meal);
            }
            String openingTime = diningHall.openingTime();
            if (openingTime.equals("")) {
                view.findViewById(R.id.dining_hall_open_close).setVisibility(View.GONE);
            } else {
                openClose.setText("Opens at " + diningHall.openingTime());
            }
        }

        if (diningHall.hasMenu()) {
            menuArrow.setVisibility(View.VISIBLE);
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
