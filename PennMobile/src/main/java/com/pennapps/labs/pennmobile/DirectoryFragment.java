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
    private String mName;
    public static final String NAME_INTENT_EXTRA = "";
    private SearchView searchView;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mAPI = new DirectoryAPI();
        mName = getArguments().getString(DirectorySearchFragment.NAME_INTENT_EXTRA);
        new GetRequestTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory, container, false);
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
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().findViewById(R.id.directory_instructions).setVisibility(View.GONE);
        MenuItem searchMenuItem = menu.findItem(R.id.directory_search);
        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
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
                args.putString(NAME_INTENT_EXTRA, arg0);
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
        private JSONArray responseArr;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject resultObj = mAPI.search(mName);
                try {
                    responseArr = (JSONArray) resultObj.get("result_data");
                } catch (NullPointerException e) {
                    return false;
                }
                if (responseArr.length() == 0) {
                    return false;
                }
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

                    Person person = new Person.Builder(resp.get("list_name").toString(),
                            resp.get("list_affiliation").toString()).
                            phone(resp.get("list_phone").toString()).
                            email(resp.get("list_email").toString()).
                            build();
                    personArr.add(person);
                }

                mAdapter = new DirectoryAdapter(mContext, personArr);
                mListView.setAdapter(mAdapter);
                getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                getActivity().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
            } catch (JSONException ignored) {

            } catch (NullPointerException ignored) {

            }
        }
    }
}
