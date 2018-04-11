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

import com.pennapps.labs.pennmobile.adapters.FlingRecyclerViewAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.FlingEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class FlingFragment extends Fragment {

    @Bind(R.id.fling_fragment_recyclerview)
    RecyclerView flingFragmentRecyclerView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fling, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.spring_fling);
        ((MainActivity) getActivity()).setNav(R.id.nav_fling);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
