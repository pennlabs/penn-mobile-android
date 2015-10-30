package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryAdapter;
import com.pennapps.labs.pennmobile.adapters.LaundryHallAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.LaundryHall;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LaundryBuildingFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private MainActivity mActivity;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry_building, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        final LaundryHall lh = getArguments().getParcelable("Laundry Hall");
        if(lh != null) {
            LaundryAdapter adapter = new LaundryAdapter(mActivity, lh.getIds());
            setListAdapter(adapter);
            loadingPanel.setVisibility(View.GONE);
            no_results.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void onResume() {
        super.onResume();
        LaundryHall lh = getArguments().getParcelable("Laundry Hall");
        if(lh != null) {
            getActivity().setTitle(lh.getName());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }

}
