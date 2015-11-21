package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.LaundryMachineAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Laundry;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class LaundryMachineTab extends ListFragment {

    private Labs mLabs;
    private MainActivity mActivity;
    private Laundry laundry;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
    private List<LaundryMachine> machines;
    private ListView mListView;
    private boolean wash;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        laundry = getArguments().getParcelable(getString(R.string.laundry));
        wash = getArguments().getInt(getString(R.string.laundry_position), 0) == 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.laundry_machine_tab, container, false);
        ButterKnife.bind(this, v);
        if(getArguments() != null && getArguments().getParcelableArray(getString(R.string.laundry_machine_intent)) != null){
            LaundryMachine[] array = (LaundryMachine[])getArguments()
                    .getParcelableArray(getString(R.string.laundry_machine_intent));
            machines = new LinkedList<>(Arrays.asList(array));
        }
        if(machines == null || machines.isEmpty()) {
            getMachines();
        } else {
            v.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            setMachines(machines, laundry);
        }
        return v;
    }

    private void getMachines(){
        mLabs.laundryMachines(laundry.hall_no)
                .subscribe(new Action1<List<LaundryMachine>>() {
                    @Override
                    public void call(final List<LaundryMachine> machines) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                try {
                                    setMachines(machines, laundry);
                                } catch (NullPointerException ignore) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
    }

    private void setMachines(List<LaundryMachine> machines, Laundry laundry){
        this.machines = machines;
        LinkedList<LaundryMachine> filtered = new LinkedList<>();
        if(wash){
            for(LaundryMachine machine: machines){
                if(machine.machine_type.contains(getString(R.string.laundry_washer))){
                    filtered.add(machine);
                }
            }
        } else{
            for(LaundryMachine machine: machines){
                if(machine.machine_type.contains(getString(R.string.laundry_dryer))){
                    filtered.add(machine);
                }
            }
        }
        LaundryMachineAdapter adapter = new LaundryMachineAdapter(mActivity, filtered, wash, laundry);
        mListView.setAdapter(adapter);
    }

    public List<LaundryMachine> returnMachines(){
        return machines;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
