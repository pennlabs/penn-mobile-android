package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jackie on 2017-10-13.
 */

public class LaundryBuildingAdapter extends BaseExpandableListAdapter {
    private List<String> laundryHalls;
    private HashMap<String, List<LaundryRoomSimple>> laundryRooms;
    private Context mContext;
    private SharedPreferences sp;
    private String s = "numRoomsSelected";
    private List<Switch> switches = new ArrayList<>();

    public LaundryBuildingAdapter(Context context, HashMap<String, List<LaundryRoomSimple>> laundryRooms, List<String> laundryHalls) {
        this.mContext = context;
        this.laundryHalls = laundryHalls;
        this.laundryRooms = laundryRooms;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        // first time
        if (sp.getInt(s, -1) == -1) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(s, 0);
            editor.apply();
        }
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

        final String laundryHallName = (String) this.getGroup(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.laundry_building_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.laundry_building_name);
        textView.setText(laundryHallName);
        ImageView imageView = (ImageView) view.findViewById(R.id.laundry_building_dropdown);

        final Switch buildingSwitch = (Switch) view.findViewById(R.id.laundry_building_favorite_switch);

        // if there is only one laundry room in the building, don't have dropdown
        if (laundryRooms.get(laundryHallName).size() == 1) {
            buildingSwitch.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            final LaundryRoomSimple laundryRoom = laundryRooms.get(laundryHallName).get(0);

            // set the Switch to the correct on or off
            buildingSwitch.setChecked(sp.getBoolean(Integer.toString(laundryRoom.id), false));

            // add the switch to the list - to aid with disabling
            if (!switches.contains(buildingSwitch)) {
                switches.add(buildingSwitch);
            }

            // max number of rooms
            if (sp.getInt(s, -1) >= 3) {
                if (!buildingSwitch.isChecked()) {
                    buildingSwitch.setEnabled(false);
                } else {
                    buildingSwitch.setEnabled(true);
                }
            }

            buildingSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = buildingSwitch.isChecked();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Integer.toString(laundryRoom.id), isChecked);
                    editor.apply();

                    // update the numRoomSelected
                    if (isChecked) {
                        editor.putInt(s, sp.getInt(s, -1) + 1);
                        editor.apply();
                    } else {
                        editor.putInt(s, sp.getInt(s, -1) - 1);
                        editor.apply();
                    }
                    updateSwitches();
                }
            });

        } else {
            buildingSwitch.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_expand);
            imageView.setVisibility(View.VISIBLE);
        }

        // if expanded
        if (b) {
            imageView.setImageResource(R.drawable.ic_collapse);
        }

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
        } else {
            textView.setText(name);
        }

        final Switch favoriteSwitch = (Switch) view.findViewById(R.id.laundry_favorite_switch);

        // set the Switch to the correct on or off
        favoriteSwitch.setChecked(sp.getBoolean(Integer.toString(laundryRoom.id), false));

        // add the switch to the list - to aid with disabling
        if (!switches.contains(favoriteSwitch)) {
            switches.add(favoriteSwitch);
        }

        // max number of rooms
        if (sp.getInt(s, -1) >= 3) {
            if (!favoriteSwitch.isChecked()) {
                favoriteSwitch.setEnabled(false);
            } else {
                favoriteSwitch.setEnabled(true);
            }
        }

        favoriteSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = favoriteSwitch.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(Integer.toString(laundryRoom.id), isChecked);
                editor.apply();

                // update the numRoomSelected
                if (isChecked) {
                    editor.putInt(s, sp.getInt(s, -1) + 1);
                    editor.apply();
                } else {
                    editor.putInt(s, sp.getInt(s, -1) - 1);
                    editor.apply();
                }

                updateSwitches();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private void updateSwitches() {

        // maximum 3 rooms selected - disable all other switches
        if (sp.getInt(s, -1) >= 3) {
            Iterator<Switch> iter = switches.iterator();
            while (iter.hasNext()) {
                Switch nextSwitch = iter.next();
                if (!nextSwitch.isChecked()) {
                    nextSwitch.setEnabled(false);
                }
            }
        }
        // less than 3 rooms selected - all switches enabled
        else {
            Iterator<Switch> iter = switches.iterator();
            while (iter.hasNext()) {
                Switch nextSwitch = iter.next();
                nextSwitch.setEnabled(true);
            }
        }
    }
}