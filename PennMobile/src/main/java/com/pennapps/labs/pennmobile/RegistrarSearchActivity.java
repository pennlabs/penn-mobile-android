package com.pennapps.labs.pennmobile;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.ResourceCursorAdapter;
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
        handleIntent(getIntent());

        sqLiteOpenHelper = new DatabaseHelper(this);
        // sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        try {
            sqLiteOpenHelper.openDatabase();
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        } catch (SQLException e) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            mAdapter = new CustomAdapter(this, R.layout.search_entry, cursor, 0);
            this.setListAdapter(mAdapter);
        }
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


