package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RegistrarListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_registrar_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment fragment = new RegistrarFragment();

        Bundle args = new Bundle();
        args.putString(RegistrarSearchFragment.COURSE_ID_EXTRA, v.getTag().toString());
        fragment.setArguments(args);

        FragmentManager fragmentManager = RegistrarSearchFragment.mFragment.getChildFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.registrar_fragment, fragment)
                       .addToBackStack(null)
                       .commit();
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        onResume();
    }

}



