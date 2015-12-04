package com.pennapps.labs.pennmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryHall;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LaundryBuildingFragment extends ListFragment {

    private MainActivity mActivity;
    private LaundryHall lh;
    @Bind(R.id.no_results) TextView no_results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        lh = getArguments().getParcelable(getString(R.string.laundry_hall_arg));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry_building, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        if (lh != null) {
            LaundryRoomAdapter adapter = new LaundryRoomAdapter(mActivity, lh.getIds());
            setListAdapter(adapter);
            no_results.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        LaundryRoom laundryRoom = lh.getIds().get(position);
        toLaundryMachine(laundryRoom);
    }

    private void toLaundryMachine(LaundryRoom laundryRoom) {
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Fragment fragment = new LaundryMachineFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.laundry), laundryRoom);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.laundry_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        if (lh != null) {
            getActivity().setTitle(lh.getName());
        }
        if (getArguments() != null) {
            int hall_no = getArguments().getInt(getString(R.string.laundry_hall_no), -1);
            if (hall_no != -1) {
                for (LaundryRoom laundryRoom : lh.getIds()) {
                    if (laundryRoom.hall_no == hall_no) {
                        getArguments().putInt(getString(R.string.laundry_hall_no), -1);
                        toLaundryMachine(laundryRoom);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
