package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


public class PCRExpListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pcr_exp, null);
        ExpandableListView expLV = (ExpandableListView) v.findViewById(R.id.list);
        expLV.setAdapter(new NewExpListViewAdapter());
        return v;
    }

    public class NewExpListViewAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;
        private Activity mActivity;

        /*
        private Course[] instructors = {
                new Course("CIS 110", "Intro to Comp Prog", "Benedict Brown", "2.69", "2.40", "3.31"),
                new Course("CIS 110", "Intro to Comp Prog", "Peter-Michael Osera", "2.95", "3.09", "2.90")
        };
        */

        private String[] instructors = {"Benedict Brown", "Peter-Michael Osera"};

        private String[][] children = {
                {"1", "2", "3"},
                {"4", "5", "6"}
        };

        // should take in ArrayList<Course>
        // where each /course/ has instructor & 3 ratings
        NewExpListViewAdapter() {

        }

        public void setInflater(LayoutInflater inflater, Activity act) {
            this.mInflater = inflater;
            mActivity = act;
        }


        @Override
        public int getGroupCount() {
            return instructors.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return instructors[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
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
            // return false;
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(PCRExpListFragment.this.getActivity());
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = new TextView(PCRExpListFragment.this.getActivity());
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}