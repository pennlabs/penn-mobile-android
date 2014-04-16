package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void startDirectory(View v) {
        Intent intent = new Intent(this, DirectorySearchActivity.class);
        /*
        Intent intent = new Intent(this, DirectoryActivity.class);
        intent.putExtra(DirectorySearchActivity.FIRST_NAME_INTENT_EXTRA, "Vivian");
        intent.putExtra(DirectorySearchActivity.LAST_NAME_INTENT_EXTRA, "Huang");
        */
        startActivity(intent);
    }

    public void startCourseSearch(View v) {
        Intent intent = new Intent(this, RegistrarSearchActivity.class);
        startActivity(intent);
    }
}
