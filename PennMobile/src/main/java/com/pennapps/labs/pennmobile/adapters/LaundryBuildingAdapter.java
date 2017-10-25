package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jackie on 2017-10-13.
 */

public class LaundryBuildingAdapter extends BaseExpandableListAdapter {
    private List<String> laundryHalls;
    private HashMap<String, List<LaundryRoomSimple>> laundryRooms;
    private Context mContext;
    private SharedPreferences sp;

    public LaundryBuildingAdapter(Context context, HashMap<String, List<LaundryRoomSimple>> laundryRooms, List<String> laundryHalls) {
        this.mContext = context;
        this.laundryHalls = laundryHalls;
        this.laundryRooms = laundryRooms;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public int getGroupCount() {
        return laundryHalls.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return laundryRooms.get(laundryHalls.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return laundryHalls.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return laundryRooms.get(laundryHalls.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // view for the laundry buildings
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String laundryHallName = (String) this.getGroup(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.laundry_building_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.laundry_building_name);
        textView.setText(laundryHallName);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final LaundryRoomSimple laundryRoom = (LaundryRoomSimple) this.getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.laundry_room_picker_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.laundry_room_name);
        String name = laundryRoom.name;
        if (name.contains("_")) {
            textView.setText(name.substring(name.indexOf("_") + 1, name.length()));
        }
        else {
            textView.setText(name);
        }

        final Switch favoriteSwitch = (Switch) view.findViewById(R.id.laundry_favorite_switch);

        // set the Switch to the correct on or off
        favoriteSwitch.setChecked(sp.getBoolean(laundryRoom.id, false));

        favoriteSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = favoriteSwitch.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(laundryRoom.id, isChecked);
                editor.apply();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}