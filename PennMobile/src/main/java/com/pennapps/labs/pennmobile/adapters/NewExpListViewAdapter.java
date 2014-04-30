package com.pennapps.labs.pennmobile.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.BusRoute;
import com.pennapps.labs.pennmobile.BusStop;
import com.pennapps.labs.pennmobile.BusStopDist;
import com.pennapps.labs.pennmobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewExpListViewAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private ArrayList<BusStopDist> mStopsList;
    // private ArrayList<BusRoute> mRoutes;
    private HashMap<String, ArrayList<BusRoute>> mRoutesByStop;

    public NewExpListViewAdapter(ArrayList<BusStopDist> stops, HashMap<String, ArrayList<BusRoute>> routesByStop, Activity activity) {
        mStopsList = stops;
        // mRoutes = routes;
        mRoutesByStop = routesByStop;
        mActivity = activity;
    }

    public void setInflater(LayoutInflater inflater, Activity act) {
        this.mInflater = inflater;
        mActivity = act;
    }

    @Override
    public int getGroupCount() {
        return mStopsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mStopsList.get(groupPosition) == null) {
            Log.v("vivlabs", "null 1");
            return 0;
        }
        if (mStopsList.get(groupPosition).getName() == null) {
            Log.v("vivlabs", "null 2");
            return 0;
        }
        if (mRoutesByStop.get(mStopsList.get(groupPosition).getName()) == null) {
            Log.v("vivlabs", "null 3 for " + mStopsList.get(groupPosition).getName());

            for (Map.Entry<String,ArrayList<BusRoute>> entry : mRoutesByStop.entrySet()) {
                //String key = entry.getKey();
                Log.v("vivlabs", entry.getKey());
                // String value = entry.getValue();
                // do stuff
            }

            return 0;
        }
        return mRoutesByStop.get(mStopsList.get(groupPosition).getName()).size();
    }

    @Override
    public BusStopDist getGroup(int groupPosition) {
        return mStopsList.get(groupPosition);
    }

    @Override
    public BusRoute getChild(int groupPosition, int childPosition) {
        return mRoutesByStop.get(mStopsList.get(groupPosition).getName()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.transit_list_item, null);
        }

        BusStopDist currentStop = getGroup(groupPosition);
        TextView transitStopName = (TextView) convertView.findViewById(R.id.transit_stop_name);
        transitStopName.setText(currentStop.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mActivity);
        textView.setText(getChild(groupPosition, childPosition).getTitle());
        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}