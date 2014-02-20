package com.pennapps.labs.pennmobile;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.sql.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

public class RegistrarSearchActivity extends ListActivity {

    CustomAdapter mAdapter;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper sqLiteOpenHelper;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_search);

        sqLiteOpenHelper = new DatabaseHelper(this);
        // sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            Log.v("vivlabs", "creating db");
            sqLiteOpenHelper.createDatabase();
            sqLiteOpenHelper.openDatabase();
            // sqLiteOpenHelper.closeDatabase();
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        } catch (SQLException e) {

        } catch (IOException e) {

        }
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.v("vivlabs", "getting intent");
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        Log.v("vivlabs", "???");
        Log.v("vivlabs", intent.getAction());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("vivlabs", query);
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            mAdapter = new CustomAdapter(this, R.layout.search_entry, cursor, 0);
            this.setListAdapter(mAdapter);
        }

        /*
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                // handles a click on a search suggestion; launches activity to show word
                Intent wordIntent = new Intent(this, WordActivity.class);
                wordIntent.setData(intent.getData());
                startActivity(wordIntent);
            } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                // handles a search query
                String query = intent.getStringExtra(SearchManager.QUERY);
                showResults(query);
            }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("vivlabs", "creating options menu?");
        getMenuInflater().inflate(R.menu.registrar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    class CustomAdapter extends ResourceCursorAdapter {

        public CustomAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
            courseId.setText(cursor.getString(cursor.getColumnIndex("id")));

            TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
            courseInstr.setText(cursor.getString(cursor.getColumnIndex("instr")));

            TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);
            courseTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        }
    }
}


