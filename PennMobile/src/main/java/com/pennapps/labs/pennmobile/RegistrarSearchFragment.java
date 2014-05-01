package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;


public class RegistrarSearchFragment extends Fragment {

    public static final String COURSE_ID_EXTRA = "COURSE_ID";
    private CourseDatabase courseDatabase;
    private Fragment mFragment;
    private Activity mActivity;
    private EditText mHeader;
    private RegistrarAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        courseDatabase = new CourseDatabase(this.getActivity().getApplicationContext());
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_registrar_search, container, false);
        mHeader = (EditText) v.findViewById(R.id.header);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mHeader.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                Cursor cursor = courseDatabase.getWordMatches(arg0.toString(), null);
                /*
                Fragment listFragment = getFragmentManager().findFragmentByTag("LIST");

                if(listFragment != null && listFragment instanceof RegistrarListFragment) {
                    Log.v("vivlabs", "list if");
                    RegistrarAdapter mAdapter = new RegistrarAdapter(mActivity.getApplicationContext(),
                            R.layout.search_entry, cursor, 0);
                    ((RegistrarListFragment) listFragment).setListAdapter(mAdapter);
                } else {
                */
                // Log.v("vivlabs", "list else");
                RegistrarListFragment listFragment = new RegistrarListFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.registrar_fragment, listFragment, "LIST").commit();
                mAdapter = new RegistrarAdapter(mActivity.getApplicationContext(),
                        R.layout.search_entry, cursor, 0);
                listFragment.setListAdapter(mAdapter);
            }
        });
    }

}


