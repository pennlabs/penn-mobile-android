package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.SupportAdapter;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.List;

public class SupportFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        ListView mListView = getListView();
        List<Person> contacts_list = new ArrayList<>();
        contacts_list.add(new Person("Penn Police General", "(215) 898-7297"));
        contacts_list.add(new Person("Penn Police Emergencies/MERT", "(215) 573-3333"));
        contacts_list.add(new Person("Penn Walk", "215-898-9255", "215-898-WALK"));
        contacts_list.add(new Person("Penn Ride", "215-898-7433", "215-898-RIDE"));
        contacts_list.add(new Person("Help Line", "215-898-4357", "215-898-HELP"));
        contacts_list.add(new Person("CAPS", "(215) 898-7021"));
        contacts_list.add(new Person("Special Services", "(215) 898-6600"));
        contacts_list.add(new Person("Women's Center", "(215) 898-8611"));
        contacts_list.add(new Person("Student Health Services", "(215) 746-3535"));
        contacts_list.add(new Person("Penn Violence Protection", "https://secure.www.upenn.edu/vpul/pvp/gethelp"));

        SupportAdapter supportAdapter = new SupportAdapter(getActivity().getApplicationContext(), contacts_list);

        mListView.setAdapter(supportAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.support);
    }
}
