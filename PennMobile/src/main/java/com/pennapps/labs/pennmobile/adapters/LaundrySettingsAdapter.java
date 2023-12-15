package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.api.StudentLife;
import com.pennapps.labs.pennmobile.classes.LaundryRequest;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.functions.Action1;

/**
 * Created by Jackie on 2017-10-13.
 */

public class LaundrySettingsAdapter extends BaseExpandableListAdapter {
    private final List<String> laundryHalls;
    private final HashMap<String, List<LaundryRoomSimple>> laundryRooms;
    private final Context mContext;
    private final SharedPreferences sp;
    private final String s;
    private final List<Switch> switches = new ArrayList<>();
    private final int maxNumRooms = 3;
    private StudentLife studentLife;
    private String bearerToken;


    public LaundrySettingsAdapter(Context context, HashMap<String, List<LaundryRoomSimple>> laundryRooms, List<String> laundryHalls) {
        this.mContext = context;
        this.laundryHalls = laundryHalls;
        this.laundryRooms = laundryRooms;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        s = mContext.getString(R.string.num_rooms_selected_pref);
        MainActivity mainActivity = (MainActivity) mContext;
        bearerToken = "Bearer " + sp.getString(mainActivity.getString(R.string.access_token), "");

        studentLife = MainActivity.getStudentLifeInstance();

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
            view = inflater.inflate(R.layout.laundry_settings_parent_item, null);
        }

        TextView textView = view.findViewById(R.id.laundry_building_name);
        textView.setText(laundryHallName);
        ImageView imageView = view.findViewById(R.id.laundry_building_dropdown);

        final Switch buildingSwitch = view.findViewById(R.id.laundry_building_favorite_switch);

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
            if (sp.getInt(s, -1) >= maxNumRooms) {
                buildingSwitch.setEnabled(buildingSwitch.isChecked());
            }

            buildingSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = buildingSwitch.isChecked();
                    SharedPreferences.Editor editor = sp.edit();
                    String id = Integer.toString(laundryRoom.id);
                    editor.putBoolean(id, isChecked);
                    editor.apply();

                    // update the numRoomSelected
                    if (isChecked) {
                        editor.putString(id + mContext.getString(R.string.location), laundryRoom.location);
                        editor.putInt(s, sp.getInt(s, -1) + 1);
                        editor.apply();
                    } else {
                        editor.putInt(s, sp.getInt(s, -1) - 1);
                        editor.apply();
                    }
                    updateSwitches();
                    sendPreferencesData();
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
            view = inflater.inflate(R.layout.laundry_settings_child_item, null);
        }

        TextView textView = view.findViewById(R.id.laundry_room_name);
        String name = laundryRoom.name;
        textView.setText(name);


        final Switch favoriteSwitch = view.findViewById(R.id.laundry_favorite_switch);

        // set the Switch to the correct on or off
        favoriteSwitch.setChecked(sp.getBoolean(Integer.toString(laundryRoom.id), false));

        // add the switch to the list - to aid with disabling
        if (!switches.contains(favoriteSwitch)) {
            switches.add(favoriteSwitch);
        }

        // max number of rooms
        if (sp.getInt(s, -1) >= maxNumRooms) {
            favoriteSwitch.setEnabled(favoriteSwitch.isChecked());
        }

        favoriteSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = favoriteSwitch.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                String id = Integer.toString(laundryRoom.id);
                editor.putBoolean(id, isChecked);
                editor.apply();

                // update the numRoomSelected
                if (isChecked) {
                    editor.putString(id + mContext.getString(R.string.location), laundryRoom.location);
                    editor.putInt(s, sp.getInt(s, -1) + 1);
                    editor.apply();
                } else {
                    editor.putInt(s, sp.getInt(s, -1) - 1);
                    editor.apply();
                }

                updateSwitches();
                sendPreferencesData();
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
        if (sp.getInt(s, -1) >= maxNumRooms) {
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

    private void getPreferencesData() {
        // warning, network call is unsafe
        studentLife = MainActivity.getStudentLifeInstance();
        studentLife.getLaundryPref(bearerToken).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    private void sendPreferencesData() {
        final List<Integer> favoriteLaundryRooms = new ArrayList<>();
        for (int i = 0; i < sp.getInt(mContext.getString(R.string.num_rooms_pref), 100); i++) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                favoriteLaundryRooms.add(i);
            }
        }

        if (favoriteLaundryRooms.isEmpty()) {
            return;
        }
        
        MainActivity mainActivity = (MainActivity) mContext;

        OAuth2NetworkManager oauth = new OAuth2NetworkManager(mainActivity);
        oauth.getAccessToken(() -> {
            bearerToken = "Bearer " + sp.getString(mainActivity.getString(R.string.access_token), "");
                    studentLife.sendLaundryPref(bearerToken, new LaundryRequest(favoriteLaundryRooms),
                            new ResponseCallback() {
                                @Override
                                public void success(Response response) {
                                    Log.i("Laundry", "Saved laundry preferences");
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e("Laundry", "Error saving laundry preferences: " + error, error);
                                }
                            });
                    return null;
                }
        );
    }
}
