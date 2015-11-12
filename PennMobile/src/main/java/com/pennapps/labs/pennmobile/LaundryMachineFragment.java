package com.pennapps.labs.pennmobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Laundry;
import com.pennapps.labs.pennmobile.classes.LaundryMachine;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Jason on 11/4/2015.
 */
public class LaundryMachineFragment extends Fragment {
    private Labs mLabs;
    private MainActivity mActivity;
    private Laundry laundry;
    @Bind(R.id.washer_ll) LinearLayout washer_ll;
    @Bind(R.id.dryer_ll) LinearLayout dryer_ll;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        Bundle args = getArguments();
        laundry = args.getParcelable("laundry");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laundry_machine, container, false);
        v.setBackgroundColor(Color.WHITE);
        ButterKnife.bind(this, v);
        ImageView washer = (ImageView) v.findViewById(R.id.washer_iv);
        washer.setMaxHeight(washer.getWidth());
        ImageView dryer = (ImageView) v.findViewById(R.id.dryer_iv);
        dryer.setMaxHeight(dryer.getWidth());
        setSummary(v);
        getMachines();
        return v;
    }

    private void setSummary(View v){
        RelativeLayout washer = (RelativeLayout) v.findViewById(R.id.washer_summary_rl);
        RelativeLayout dryer = (RelativeLayout) v.findViewById(R.id.dryer_summary_rl);
        LaundryFragment.setSummary(laundry.washers_available, laundry.washers_in_use, 4, washer, mActivity);
        LaundryFragment.setSummary(laundry.dryers_available, laundry.dryers_in_use, 5, dryer, mActivity);
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
                                    setMachineData(machines);
                                } catch(NullPointerException ignore){
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

    private void setMachineData(List<LaundryMachine> machines){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 5);
        for (LaundryMachine machine: machines){
            LinearLayout currentLayout;
            TextView textView = new TextView(getContext());
            if(machine.machine_type.equalsIgnoreCase("Front-Load Washer")){
                currentLayout = washer_ll;
                String string = getString(R.string.laundry_washer_textview) + " " + machine.number;
                textView.setText(string);
            }else{
                currentLayout = dryer_ll;
                String string = getString(R.string.laundry_dryer_textview) + " " + machine.number;
                textView.setText(string);
            }
            textView.setTextSize(15);
            currentLayout.addView(textView, params);
            TextView details = new TextView(getContext());
            if(machine.available){
                details.setTextColor(ContextCompat.getColor(getContext(), R.color.circle_color_green));
                details.setText(R.string.laundry_available);
            } else{
                details.setTextColor(ContextCompat.getColor(getContext(), R.color.circle_color_red));

                if(machine.time_left != null && machine.time_left.charAt(0) == 'n'){
                    details.setText(machine.time_left);
                } else if(machine.time_left != null) {
                    String string = getString(R.string.laundry_availablein);
                    string = machine.time_left + " " + string;
                    details.setText(string);
                }
            }
            currentLayout.addView(details, params);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(laundry.name != null) {
            getActivity().setTitle(laundry.name);
        }
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), LaundryBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("hall_no", laundry.hall_no);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, alarmIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle(R.string.laundry);
        ButterKnife.unbind(this);
    }
}
