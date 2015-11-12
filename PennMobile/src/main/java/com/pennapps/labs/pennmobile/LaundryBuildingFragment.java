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

import com.pennapps.labs.pennmobile.adapters.LaundryAdapter;
import com.pennapps.labs.pennmobile.classes.Laundry;
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
        lh = getArguments().getParcelable("Laundry Hall");
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
            LaundryAdapter adapter = new LaundryAdapter(mActivity, lh.getIds());
            setListAdapter(adapter);
            no_results.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Laundry laundry = lh.getIds().get(position);
        toLaundryMachine(laundry);
    }

    private void toLaundryMachine(Laundry laundry) {
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Fragment fragment = new LaundryMachineFragment();
        Bundle args = new Bundle();
        args.putParcelable("laundry", laundry);
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
            int hall_no = getArguments().getInt("hall_no", -1);
            if (hall_no != -1) {
                for (Laundry laundry : lh.getIds()) {
                    if (laundry.hall_no == hall_no) {
                        getArguments().putInt("hall_no", -1);
                        toLaundryMachine(laundry);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
