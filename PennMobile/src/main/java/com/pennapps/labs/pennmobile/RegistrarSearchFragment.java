package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;


public class RegistrarSearchFragment extends ListFragment {


    public static final String COURSE_ID_EXTRA = "COURSE_ID";
    private CustomAdapter mAdapter;
    private CourseDatabase courseDatabase;
    private ListFragment mListFragment;
    private Activity mActivity;
    private EditText mHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        courseDatabase = new CourseDatabase(this.getActivity().getApplicationContext());
        mListFragment = this;
        mAdapter = new CustomAdapter(mListFragment.getActivity().getApplicationContext(),
                R.layout.search_entry, null, 0);
        mListFragment.setListAdapter(mAdapter);
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
                mAdapter = new CustomAdapter(mActivity.getApplicationContext(),
                        R.layout.search_entry, cursor, 0);
                mListFragment.setListAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        /*
        Intent intent = new Intent(this, RegistrarActivity.class);
        intent.putExtra(COURSE_ID_EXTRA, v.getTag().toString());
        startActivity(intent);
        */
        Fragment fragment = new RegistrarFragment();

        Bundle args = new Bundle();
        args.putString(RegistrarSearchFragment.COURSE_ID_EXTRA, v.getTag().toString());
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    class CustomAdapter extends ResourceCursorAdapter {

        public CustomAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            /*
            Log.v("adel", "" + cursor.getCount());
            Log.v("adel", "" + cursor.getColumnCount());
            */

            // Log.v("vivlabs", Arrays.toString(cursor.getColumnNames()));

            TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
            courseId.setText(cursor.getString(cursor.getColumnIndex("course_id")));

            TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
            courseInstr.setText(cursor.getString(cursor.getColumnIndex("instructor")));

            TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);
            courseTitle.setText(cursor.getString(cursor.getColumnIndex("course_title")));

            view.setTag(cursor.getString(cursor.getColumnIndex("course_id")));
        }
    }
}


