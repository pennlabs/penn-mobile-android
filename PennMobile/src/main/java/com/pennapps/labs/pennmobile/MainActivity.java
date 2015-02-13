package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.adapters.NavDrawerListAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.api.Serializer;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mFeatureTitles;
    private Labs mLabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        mFeatureTitles = new String[]{"Home", "Courses", "Directory", "Buildings", "Dining", "Transit", "News", "Support", "About"};
        int[] icons = new int[]{R.drawable.ic_home, R.drawable.ic_book, R.drawable.ic_contacts,
                R.drawable.ic_restaurant, R.drawable.ic_directions_bus, R.drawable.ic_announcement,
                R.drawable.ic_help, R.drawable.ic_info
        };

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ArrayList<NavDrawerItem> mFeatureList = createNavDrawerItems(mFeatureTitles, icons);
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, mFeatureList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set default fragment to MainFragment
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new MainFragment());
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        super.onBackPressed();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            closeKeyboard();
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private ArrayList<NavDrawerItem> createNavDrawerItems(String[] navbarItems, int[] icons) {
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();
        for (int i = 0; i < navbarItems.length; i++) {
            navDrawerItems.add(new NavDrawerItem(navbarItems[i], icons[i]));
        }
        return navDrawerItems;
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new MainFragment();
        } if (position == 1) {
            fragment = new RegistrarSearchFragment();
        } else if (position == 2) {
            fragment = new DirectorySearchFragment();
        } else if (position == 3) {
            fragment = new BuildingFragment();
        } else if (position == 4) {
            fragment = new DiningFragment();
        } else if (position == 5) {
            fragment = new TransitFragment();
        } else if (position == 6) {
            fragment = new EventsFragment();
        } else if (position == 7) {
            fragment = new SupportFragment();
        } else if (position == 8) {
            fragment = new AboutFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackEntryCount == 0) { // If we are on the home screen then we should always
                    setTitle("PennMobile");     // set the title to PennMobile
                }
            }
        });

        mDrawerList.setItemChecked(position, true);
        setTitle(mFeatureTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void onHomeButtonClick(View v) {
        if (v.getId() == R.id.registrar_img || v.getId() == R.id.registrar_cont || v.getId() == R.id.registrar_button) {
            selectItem(1);
        } else if (v.getId() == R.id.directory_img || v.getId() == R.id.directory_cont || v.getId() == R.id.directory_button) {
            selectItem(2);
        } else if (v.getId() == R.id.dining_img || v.getId() == R.id.dining_cont || v.getId() == R.id.dining_button) {
            selectItem(3);
        } else if (v.getId() == R.id.transit_img || v.getId() == R.id.transit_cont || v.getId() == R.id.transit_button) {
            selectItem(4);
        } else if (v.getId() == R.id.news_img || v.getId() == R.id.news_cont || v.getId() == R.id.news_button) {
            selectItem(5);
        }
    }

    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            if (title.equals("Home")) {
                getSupportActionBar().setTitle("PennMobile");
            } else {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    public Labs getLabsInstance() {
        if (mLabs == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Course>>(){}.getType(), new Serializer.CourseSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Person>>(){}.getType(), new Serializer.DataSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Venue>>(){}.getType(), new Serializer.VenueSerializer());
            gsonBuilder.registerTypeAdapter(NewDiningHall.class, new Serializer.MenuSerializer());
            Gson gson = gsonBuilder.create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint("http://api.pennlabs.org")
                    .build();
            mLabs = restAdapter.create(Labs.class);
        }
        return mLabs;
    }
}
