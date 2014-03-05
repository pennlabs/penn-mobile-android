package com.pennapps.labs.pennmobile;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

public class RegistrarSearchActivity extends ListActivity
        implements SearchView.OnQueryTextListener {

    private CustomAdapter mAdapter;
    private CourseDatabase courseDatabase;
    private String query;
    private SearchView mSearchView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_search);
        mListView = getListView();
        mListView.setTextFilterEnabled(true);
        courseDatabase = new CourseDatabase(this);
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Cursor cursor = courseDatabase.getWordMatches(query, null);

            mAdapter = new CustomAdapter(this, R.layout.search_entry, cursor, 0);
            this.setListAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registrar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v("vivlabs", "calling onQueryTextChange");
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
            // mSearchView.setQuery(newText, false);
        }
        return true;
        // return false;
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
            Log.v("adel", Arrays.toString(cursor.getColumnNames()));
            */

            TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
            courseId.setText(cursor.getString(cursor.getColumnIndex("course_id")));

            TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
            courseInstr.setText(cursor.getString(cursor.getColumnIndex("instructor")));

            TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);
            courseTitle.setText(cursor.getString(cursor.getColumnIndex("course_title")));
        }
    }
}


