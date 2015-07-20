package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MenuFragment extends Fragment {

    private DiningHall mDiningHall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        v.setBackgroundColor(Color.WHITE);
        TextView diningHallNameTV = (TextView) v.findViewById(R.id.dining_hall_name);
        diningHallNameTV.setText(mDiningHall.getName());
        diningHallNameTV.setGravity(Gravity.LEFT);
        fillDescriptions(v);
        return v;
    }

    public void fillDescriptions(View view) {
        TextView hallNameTV = (TextView) view.findViewById(R.id.dining_hall_name);
        TextView hallStatus = (TextView) view.findViewById(R.id.dining_hall_status);
        LinearLayout menuParent = (LinearLayout) view.findViewById(R.id.menu_parent);

        hallNameTV.setText(WordUtils.capitalizeFully(mDiningHall.getName()));
        view.setTag(mDiningHall);
        if (mDiningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.label_red));
        }

        StringBuilder menuText = new StringBuilder();
        for (Map.Entry<String, HashMap<String, HashSet<String>>> menu : mDiningHall.menus.entrySet()) {
            menuText.setLength(0);
            String mealName = StringUtils.capitalize(menu.getKey());
            for (Map.Entry<String, HashSet<String>> menuItem : menu.getValue().entrySet()) {
                String key = StringUtils.capitalize(menuItem.getKey());
                HashSet<String> items = menuItem.getValue();
                String tab = "&nbsp&nbsp&nbsp ";
                menuText.append("<b>");
                menuText.append(key);
                menuText.append("</b> <br>");
                for (String item : items) {
                    menuText.append(tab);
                    menuText.append(item);
                    menuText.append("<br>");
                }
            }
            // Meal name
            TextView mealNameTV = new TextView(view.getContext(), null, R.style.MealName);
            mealNameTV.setVisibility(View.VISIBLE);
            mealNameTV.setText(mealName);
            menuParent.addView(mealNameTV);
            // Menu
            TextView menuTV = new TextView(view.getContext(), null, R.style.Menu);
            menuTV.setVisibility(View.VISIBLE);
            menuTV.setText(Html.fromHtml(menuText.toString()));
            menuParent.addView(menuTV);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
    }
}
