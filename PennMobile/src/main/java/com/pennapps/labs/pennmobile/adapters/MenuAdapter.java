package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lily on 2/8/2016.
 */
public class MenuAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headers;
    // child data in format of header title, child title
    private HashMap<String, List<String>> children;

    public MenuAdapter(Context context, List<String> headers,
                       HashMap<String, List<String>> children) {
        this.context = context;
        this.headers = headers;
        this.children = children;
    }
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.children.get(this.headers.get(groupPosition))
                .get(childPosititon);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.children.get(this.headers.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_station, null);
        }
        convertView.setPadding(0, 0, 0, 0);
        TextView listHeader = (TextView) convertView
                .findViewById(R.id.station_name);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headerTitle);

        ImageView expandArrow = (ImageView) convertView.findViewById(R.id.station_expand);
        ImageView collapseArrow = (ImageView) convertView.findViewById(R.id.station_collapse);

        if (isExpanded){
            collapseArrow.setVisibility(View.VISIBLE);
            expandArrow.setVisibility(View.INVISIBLE);
            listHeader.setTextColor(context.getResources().getColor(R.color.color_primary));
        }
        else{
            collapseArrow.setVisibility(View.INVISIBLE);
            expandArrow.setVisibility(View.VISIBLE);
            listHeader.setTextColor(context.getResources().getColor(R.color.black));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_food_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.menu_food_description);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
