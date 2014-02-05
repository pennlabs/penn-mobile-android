package com.pennapps.labs.pennmobile;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RegistrarSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_search);
        handleIntent(getIntent());
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
