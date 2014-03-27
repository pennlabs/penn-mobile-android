package com.pennapps.labs.pennmobile;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarActivity extends Activity {

    private RegistrarAPI mAPI;
    private TextView mTextView;
    private FragmentManager mFragmentMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        mAPI = new RegistrarAPI();
        mTextView = (TextView) findViewById(R.id.temp);
        mFragmentMgr = getFragmentManager();

        Intent intent = getIntent();
        new GetRequestTask(intent.getStringExtra(RegistrarSearchActivity.COURSE_ID_EXTRA)).execute();
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private String input;
        private JSONObject resp;

        GetRequestTask(String s) {
            input = s;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                JSONObject resultObj = mAPI.getCourse(input);
                JSONArray responseArr = (JSONArray) resultObj.get("result_data");
                // Log.v("vivlabs", resultObj.toString());
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
                mFragmentMgr.beginTransaction().
                        hide(mFragmentMgr.findFragmentById(R.id.map)).commit();
                mTextView.setText(input + " is not currently offered.");
                // sort of sloppy :/
                return;
            }
            try {
                // Log.v("vivlabs", resp.toString());
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
                                        room_number(meetings.get("room_number").toString()).
                                        start_time(meetings.get("start_time").toString()).
                                        end_time(meetings.get("end_time").toString()).
                                        section_id(meetings.get("section_id_normalized").toString()).
                                        build();

                String displayText = course.getCourseDept() + " " + course.getCourseNumber() + "\n" +
                                     course.getCourseTitle() + "\n";

                for (int i = 0; i < course.getInstructors().length; i++) {
                    displayText += course.getInstructors()[i] + "\n";
                }

                displayText += course.getActivity() + "\n" +
                               course.getBuildingCode() + " " + course.getRoomNumber() + "\n";

                mTextView.setText(displayText);
            } catch (JSONException e) {
                // Log.v("vivlabs", e.toString());
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
