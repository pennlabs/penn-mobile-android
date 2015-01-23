package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.NavDrawerItem;
import com.pennapps.labs.pennmobile.R;

import java.util.ArrayList;

/**
 * List adapter for drawer items
 * Created by Adel Qalieh on 11/16/14.
 */
public class NavDrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int i) {
        return navDrawerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            view = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView txtTitle = (TextView) view.findViewById(R.id.nav_text_title);
        ImageView imgIcon = (ImageView) view.findViewById(R.id.nav_img_icon);

        txtTitle.setText(navDrawerItems.get(i).mTitle);
        imgIcon.setImageResource(navDrawerItems.get(i).mIcon);

        return view;
    }
}
