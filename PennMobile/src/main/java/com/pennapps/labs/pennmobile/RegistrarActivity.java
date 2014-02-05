package com.pennapps.labs.pennmobile;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONObject;

public class RegistrarActivity extends Activity {

    private RegistrarAPI mAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        mAPI = new RegistrarAPI();
        setEditText();
    }

    private void setEditText() {
        TextView tv = (TextView) findViewById(R.id.search_edit_text);
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // JSONObject jsonRes = mAPI.getCourse(s.toString());
                // Log.v("vivlabs", jsonRes.toString());
                new GetRequestTask(s.toString()).execute();
            }
        });
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Void> {

        private String input;
        private JSONObject response;

        GetRequestTask(String s) {
            input = s;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            response = mAPI.getCourse(input);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.v("vivlabs", response.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registrar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
    
}
