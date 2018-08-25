package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.pennapps.labs.pennmobile.adapters.FlingRecyclerViewAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.FlingEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;

public class FlingFragment extends Fragment {

    @BindView(R.id.fling_fragment_recyclerview) RecyclerView flingFragmentRecyclerView;
    private Unbinder unbinder;

    List<FlingEvent> flingEvents;

    public FlingFragment() {
        // Required empty public constructor
    }

    public static FlingFragment newInstance() {
        FlingFragment fragment = new FlingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Spring Fling")
                .putContentType("App Feature")
                .putContentId("7"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fling, container, false);
        unbinder = ButterKnife.bind(this, view);
        Labs labs = MainActivity.getLabsInstance();
        labs.getFlingEvents().subscribe(new Action1<List<FlingEvent>>() {
            @Override
            public void call(final List<FlingEvent> flingEvents) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flingFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        flingFragmentRecyclerView.setAdapter(new FlingRecyclerViewAdapter(getContext(), flingEvents));
                    }
                });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Error: Could not retrieve Spring Fling schedule", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.spring_fling);
//        ((MainActivity) getActivity()).setNav(R.id.nav_fling);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
