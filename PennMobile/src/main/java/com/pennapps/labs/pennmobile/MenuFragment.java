package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment {

    private DiningHall mDiningHall;
    private MainActivity mActivity;
    @Bind(R.id.menu_parent) LinearLayout menuParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiningHall = getArguments().getParcelable("DiningHall");
        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.dining_info_button:
                Fragment fragment = new DiningInfoFragment();
                Bundle args = new Bundle();
                args.putParcelable("DiningHall", getArguments().getParcelable("DiningHall"));
                fragment.setArguments(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.dining_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        for (DiningHall.Menu menu : mDiningHall.menus) {
            addDiningTextView(R.style.MealName, StringUtils.capitalize(menu.name));
            for (DiningHall.DiningStation station : menu.stations) {
                addDiningTextView(R.style.DiningStation, StringUtils.capitalize(station.name));
                for (DiningHall.FoodItem item : station.items) {
                    addDiningTextView(R.style.FoodItem, StringEscapeUtils.unescapeXml(item.title));
                }
            }
        }
    }

    private void addDiningTextView(@StyleRes int style, String text) {
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
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        getActivity().setTitle(mDiningHall.getName() + " Menu");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.dining);
        ButterKnife.unbind(this);
    }
}