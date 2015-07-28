package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment {

    private DiningHall mDiningHall;
    private MainActivity mActivity;

    @Bind(R.id.dining_hall_name) TextView hallNameTV;
    @Bind(R.id.dining_hall_status) TextView hallStatus;
    @Bind(R.id.menu_parent) LinearLayout menuParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        fillDescriptions();
        return v;
    }

    public void fillDescriptions() {
        hallNameTV.setText(WordUtils.capitalizeFully(mDiningHall.getName()));
        if (mDiningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.label_red));
        }

        for (Map.Entry<String, HashMap<String, HashSet<String>>> menu : mDiningHall.menus.entrySet()) {
            addDiningTextView(R.style.MealName, StringUtils.capitalize(menu.getKey()));
            for (Map.Entry<String, HashSet<String>> menuItem : menu.getValue().entrySet()) {
                addDiningTextView(R.style.DiningStation, StringUtils.capitalize(menuItem.getKey()));
                for (String item : menuItem.getValue()) {
                    addDiningTextView(R.style.FoodItem, item);
                }
            }
        }
    }

    private void addDiningTextView(int style, String text) {
        TextView textView = new TextView(mActivity);
        textView.setTextAppearance(mActivity, style);
        textView.setText(text);
        if (style == R.style.FoodItem) {
            textView.setPadding(50, 0, 0, 0);
        } else if (style == R.style.MealName) {
            textView.setPadding(0, 25, 0, 25);
        }
        menuParent.addView(textView);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.dining);
        ButterKnife.unbind(this);
    }
}