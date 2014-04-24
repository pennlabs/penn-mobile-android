package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectoryFragment extends ListFragment {

    private DirectoryAPI mAPI;
    private ListView mListView;
    private DirectoryAdapter mAdapter;
    private Context mContext;
    private String mFirstName;
    private String mLastName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mAPI = new DirectoryAPI();
        mAPI.setUrlPath("directory?");
        mFirstName = getArguments().getString(DirectorySearchFragment.FIRST_NAME_INTENT_EXTRA);
        mLastName = getArguments().getString(DirectorySearchFragment.LAST_NAME_INTENT_EXTRA);
        new GetRequestTask(mFirstName, mLastName).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_directory, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private String urlParameter;
        private JSONArray responseArr;

        GetRequestTask(String firstName, String lastName) {
            urlParameter = "first_name=" + firstName + "&last_name=" + lastName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getCourse(urlParameter);
                responseArr = (JSONArray) resultObj.get("result_data");
                if (responseArr.length() == 0) {
                    return false;
                }
                // Log.v("vivlabs", "LOL " + responseArr.toString());
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
                ArrayList<Person> personArr = new ArrayList<Person>();
                JSONObject resp;

                for (int i = 0; i < responseArr.length(); i++) {
                    resp = (JSONObject) responseArr.get(i);
                    // Log.v("vivlabs", resp.toString());

                    Person person = new Person.Builder(resp.get("list_name").toString(),
                            resp.get("list_affiliation").toString()).
                            phone(resp.get("list_phone").toString()).
                            email(resp.get("list_email").toString()).
                            build();
                    personArr.add(person);

                }

                mAdapter = new DirectoryAdapter(mContext, personArr);
                mListView.setAdapter(mAdapter);
            } catch (JSONException e) {
                // Log.v("vivlabs", e.toString());
            }
        }
    }
}
