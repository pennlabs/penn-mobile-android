package com.pennapps.labs.pennmobile;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.SupportAdapter;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.List;

public class SupportFragment extends ListFragment {

    private MainActivity mActivity;
    private List<Person> contacts_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        ListView mListView = getListView();
        contacts_list = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mActivity.getMenuInflater().inflate(R.menu.phone_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.support_contacts_add:
                SaveContactsFragment frag = new SaveContactsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), frag, "SAVE_CONTACTS_FRAGMENT")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.removeTabs();
        getActivity().setTitle(R.string.support);
        if (Build.VERSION.SDK_INT > 17){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setSelectedTab(10);
        }
    }
}
