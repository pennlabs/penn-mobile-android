package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link ListFragment} subclass.
 */
public class SupportFragment extends ListFragment {

    private ListView mListView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mListView = getListView();
        List<String> your_array_list = new ArrayList<>();
        your_array_list.add("foo");
        your_array_list.add("bar");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                R.layout.support_list_item,
                your_array_list);
        mListView.setAdapter(arrayAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }


}