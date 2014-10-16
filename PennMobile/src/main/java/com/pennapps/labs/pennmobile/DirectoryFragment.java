package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;
import com.pennapps.labs.pennmobile.api.DirectoryAPI;
import com.pennapps.labs.pennmobile.classes.Person;

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
    public static final String FIRST_NAME_INTENT_EXTRA = "FIRST_NAME";
    public static final String LAST_NAME_INTENT_EXTRA = "LAST_NAME";
    private SearchView searchView;
    private TextView textView;

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
        View v = inflater.inflate(R.layout.fragment_directory, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.directory_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);

        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO: error check for filled in fields
                Fragment fragment = new DirectoryFragment();
                Bundle args = new Bundle();
                String[] query = arg0.split("\\s+");
                if (query.length == 0) {
                    args.putString(FIRST_NAME_INTENT_EXTRA, "");
                    args.putString(FIRST_NAME_INTENT_EXTRA, "");
                } else if (query.length == 1) {
                    args.putString(FIRST_NAME_INTENT_EXTRA, query[0].replaceAll("\\s+",""));
                    args.putString(LAST_NAME_INTENT_EXTRA, "");
                } else {
                    args.putString(FIRST_NAME_INTENT_EXTRA, query[0].replaceAll("\\s+",""));
                    args.putString(LAST_NAME_INTENT_EXTRA, query[1].replaceAll("\\s+",""));
                }
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                return true;
            }
        };
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(queryListener);
    }

    private class GetRequestTask extends AsyncTask<Void, Void, Boolean> {
        private String urlParameter;
        private JSONArray responseArr;

        GetRequestTask(String firstName, String lastName) {
            if (firstName.equals("") && lastName.equals("")) {
                urlParameter = "";
            } else if (lastName.equals("")) {
                urlParameter = "first_name=" + firstName;
            } else {
                urlParameter = "first_name=" + firstName + "&last_name=" + lastName;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.getAPIData(urlParameter);
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
