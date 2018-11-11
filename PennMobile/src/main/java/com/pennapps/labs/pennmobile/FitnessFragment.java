package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Gym;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;


public class FitnessFragment extends Fragment {

    // bind recycler view
    @BindView(R.id.gym_list) RecyclerView fitnessRecyclerView;
    private Unbinder unbinder;

    public FitnessFragment() {
        // Required empty public constructor
    }

    public static FitnessFragment newInstance() {
        FitnessFragment fragment = new FitnessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fitness")
                .putContentType("App Feature")
                .putContentId("9"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitness, container, false);
        unbinder = ButterKnife.bind(this, view);

        // set layout manager for RecyclerView
        fitnessRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        // add divider
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        fitnessRecyclerView.addItemDecoration(divider);

        // get API data
        Labs labs = MainActivity.getLabsInstance();
        labs.getGymData().subscribe(new Action1<List<Gym>>() {
            @Override
            public void call(final List<Gym> gyms) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("REMOVE", "IT DID GET HERE 1");
                        fitnessRecyclerView.setAdapter(new FitnessAdapter(getContext(), gyms));
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getActivity(), "Error: Could not load gym information", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }
}
