package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectoryActivity extends Activity {

    private DirectoryAPI mAPI;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        mTextView = (TextView) findViewById(R.id.tempPerson);

        Intent intent = getIntent();
        mAPI = new DirectoryAPI();
        String mFirstName = intent.getStringExtra(DirectorySearchActivity.FIRST_NAME_INTENT_EXTRA);
        String mLastName = intent.getStringExtra(DirectorySearchActivity.LAST_NAME_INTENT_EXTRA);
        mAPI.setUrlPath("directory?");
        new GetRequestTask(mFirstName, mLastName).execute();
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private String urlParameter;
        private JSONObject resp;

        GetRequestTask(String firstName, String lastName) {
            urlParameter = "first_name=" + firstName + "&last_name=" + lastName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getCourse(urlParameter);
                JSONArray responseArr = (JSONArray) resultObj.get("result_data");
                if (responseArr.length() == 0) {
                    return false;
                }
                resp = (JSONObject) responseArr.get(0);
                return true;
            } catch(JSONException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            if (!valid) {
                // TODO:
                return;
            }
            try {
                Person person = new Person.Builder(resp.get("list_name").toString(),
                                                   resp.get("list_affiliation").toString()).
                                                   phone(resp.get("list_phone").toString()).
                                                   email(resp.get("list_email").toString()).
                                                   build();

                String displayText = person.getName() + "\n" +
                                     person.getAffiliation() + "\n" +
                                     person.getEmail() + "\n" +
                                     person.getPhone();

                mTextView.setText(displayText);
            } catch (JSONException e) {
                // Log.v("vivlabs", e.toString());
            }
        }
    }

}
