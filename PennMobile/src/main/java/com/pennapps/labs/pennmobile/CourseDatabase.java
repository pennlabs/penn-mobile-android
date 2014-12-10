package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;

public class CourseDatabase {
    private static final String FTS_VIRTUAL_TABLE = "courses";
    private final DatabaseHelper mDatabaseOpenHelper;
    private static final HashMap<String, String> mColumnMap = buildColumnMap();

    public CourseDatabase(Context context) {
        mDatabaseOpenHelper = new DatabaseHelper(context);

        try {
            mDatabaseOpenHelper.createDatabase();
            mDatabaseOpenHelper.openDatabase();
            // sqLiteDatabase = mDatabaseOpenHelper.getReadableDatabase();
        } catch (SQLException | IOException ignored) {

        }
    }

    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        map.put("course_id", "course_id");
        map.put("course_title", "course_title");
        map.put("instructor", "instructor");
        /*
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        */
        return map;
    }

    public Cursor getWord(String rowId, String[] columns) {
        String selection = "rowId = ?";
        String[] selectionArgs = new String[] {rowId};
        return query(selection, selectionArgs, columns);
    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection =
                "course_id" + " LIKE ?" + " OR " +
                "course_title" + " LIKE ?" + " OR " +
                "instructor" + " LIKE ?";
        String[] selectionArgs = new String[] {query+"%", query+"%", query+"%"};
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] args, String[] cols) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        builder.setProjectionMap(mColumnMap);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                cols, selection, args, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private String DB_PATH = "";
        private String DB_NAME = "registrar.db";
        private SQLiteDatabase mDatabase;
        private final Context mContext;

        public DatabaseHelper(Context context) {
            super(context, "registrar.db", null, 1);
            mContext = context;
            DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        }


        // creates an empty database on the system and rewrites with internal database
        public void createDatabase() throws IOException {
            boolean dbExist = checkDatabase();

            if (!dbExist) {
                this.getReadableDatabase();

                try {
                    copyDatabase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }

            // mDatabase.execSQL(FTS_TABLE_CREATE);
        }


        // check if the database already exist to avoid re-copying the file
        private boolean checkDatabase(){
            SQLiteDatabase checkDB = null;
            try {
                String myPath = DB_PATH + DB_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                // database doesn't exist yet.
            }

            if (checkDB != null) {
                checkDB.close();
            }
            return checkDB != null;
        }

        /**
         * Copies your database from your local assets-folder to the just created empty
         * database in the system folder, from where it can be accessed and handled.
         * This is done by transferring the byte stream.
         * */
        private void copyDatabase() throws IOException {

            // open your local db as the input stream
            InputStream myInput = mContext.getAssets().open(DB_NAME);

            // path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            // open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        public void openDatabase() throws SQLException {
            String myPath = DB_PATH + DB_NAME;
            mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }

        @Override
        public synchronized void close() {
            if (mDatabase != null) {
                mDatabase.close();
            }
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        // Add your public helper methods to access and get content from the database.
        // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
        // to you to create adapters for your views.
    }
}