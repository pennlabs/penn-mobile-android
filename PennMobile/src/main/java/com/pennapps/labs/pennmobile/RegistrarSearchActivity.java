package com.pennapps.labs.pennmobile;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;

public class RegistrarSearchActivity extends ListActivity {

    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_search);
        handleIntent(getIntent());

        getResources().getResourceName(R)
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(mContext, ?, null, ?);
        String[] columns = new String[] {Course.ID, Course.INSTR, Course.TITLE};
        int[] to = new int[] {R.id.course_id_text, R.id.course_instr_text, R.id.course_title_text};
        Cursor cursor = getContentResolver().query();

        mAdapter = new SimpleCursorAdapter(this, R.layout.search_entry, cursor, columns, to, 1);
        this.setListAdapter(mAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }
}
