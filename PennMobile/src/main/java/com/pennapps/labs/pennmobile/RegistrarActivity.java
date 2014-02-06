package com.pennapps.labs.pennmobile;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarActivity extends Activity {

    private RegistrarAPI mAPI;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        mAPI = new RegistrarAPI();
        mTextView = (TextView) findViewById(R.id.temp);
        new GetRequestTask("").execute();
        // setEditText();
    }

    private void setEditText() {
        /*
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
        */
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Void> {

        private String input;
        private JSONObject resp;

        GetRequestTask(String s) {
            input = s;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONArray responseArr = (JSONArray) ((JSONObject) mAPI.getCourse(input)).get("result_data");
                resp = (JSONObject) responseArr.get(0);
                return null;
            } catch(JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                JSONObject meetings = (JSONObject) ((JSONArray) resp.get("meetings")).get(0);
                JSONArray instrJSON = (JSONArray) resp.get("instructors");
                String[] instrArr = new String[instrJSON.length()];
                for (int i = 0; i < instrJSON.length(); i++) {
                    instrArr[i] = ((JSONObject) instrJSON.get(i)).get("name").toString();
                }

                RegCourse course = new RegCourse.Builder(resp.get("activity").toString(),
                                        resp.get("course_department").toString(),
                                        resp.get("course_number").toString()).
                                        course_description(resp.get("course_description").toString()).
                                        course_title(resp.get("course_title").toString()).
                                        instructors(instrArr).
                                        building_code(meetings.get("building_code").toString()).
                                        building_name(meetings.get("building_name").toString()).
                                        start_time(meetings.get("start_time").toString()).
                                        end_time(meetings.get("end_time").toString()).
                                        section_id(meetings.get("section_id_normalized").toString()).
                                        build();

                // Log.v("vivlabs", course.getCourseDept() + " " + course.getCourseNumber());
                mTextView.setText(course.getCourseDept() + " " + course.getCourseNumber());
            } catch (JSONException e) {
                Log.v("vivlabs", e.toString());
            }
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
