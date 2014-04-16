package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class DirectorySearchActivity extends Activity {

    private EditText mFirstName;
    private EditText mLastName;
    public static final String FIRST_NAME_INTENT_EXTRA = "FIRST_NAME";
    public static final String LAST_NAME_INTENT_EXTRA = "LAST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_search);
        mFirstName = (EditText) findViewById(R.id.directory_first_name);
        mLastName = (EditText) findViewById(R.id.directory_last_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directory_search, menu);
        return true;
    }

    public void searchDirectory(View view) {
        // TODO: error check for filled in fields
        Intent intent = new Intent(this, DirectoryActivity.class);
        intent.putExtra(FIRST_NAME_INTENT_EXTRA, mFirstName.getText().toString());
        intent.putExtra(LAST_NAME_INTENT_EXTRA, mLastName.getText().toString());
        startActivity(intent);
    }
}
