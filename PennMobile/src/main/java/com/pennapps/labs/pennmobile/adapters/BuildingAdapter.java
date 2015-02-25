package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Building;

import java.util.List;

public class BuildingAdapter extends ArrayAdapter<Building> {

    public BuildingAdapter(Context context, List<Building> buildings) {
        super(context, R.layout.building_list_item, buildings);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Building building = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.building_list_item, null);

        TextView name = (TextView) view.findViewById(R.id.tv_building_name);
        TextView address = (TextView) view.findViewById(R.id.tv_building_address);
        TextView http_link = (TextView) view.findViewById(R.id.tv_building_http_link);

        name.setText(building.title);
        address.setText(building.address);
        http_link.setText(building.http_link);
        return view;
    }
}