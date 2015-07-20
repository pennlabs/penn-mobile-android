package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.api.Serializer;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.Venue;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private Labs mLabs;
    private OkHttpClient mAPIClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {};
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList = (NavigationView) findViewById(R.id.navigation);
        mDrawerList.setNavigationItemSelectedListener(new DrawerItemClickListener());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

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

        try {
            WebView webView = NewsTab.currentWebView;
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } catch (NullPointerException ignored) {
            // No webview exists currently
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.main_title);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

    private class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            item.setChecked(true);
            switch (id) {
                case R.id.navHome:
                    selectItem(0);
                    break;
                case R.id.navRegistrar:
                    selectItem(1);
                    break;
                case R.id.navDirectory:
                    selectItem(2);
                    break;
                case R.id.navDining:
                    selectItem(3);
                    break;
                case R.id.navTransit:
                    selectItem(4);
                    break;
                case R.id.navNews:
                    selectItem(5);
                    break;
                case R.id.navMap:
                    selectItem(6);
                    break;
                case R.id.navSupport:
                    selectItem(7);
                    break;
                case R.id.navAbout:
                    selectItem(8);
                    break;
            }
            return false;
        }
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
            fragment = new DiningFragment();
        } else if (position == 4) {
            fragment = new TransitFragment();
        } else if (position == 5) {
            fragment = new NewsFragment();
        } else if (position == 6) {
            fragment = new MapFragment();
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
        } else if (v.getId() == R.id.map_img || v.getId() == R.id.map_cont || v.getId() == R.id.map_button) {
            selectItem(6);
        }
    }

    public Labs getLabsInstance() {
        if (mLabs == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Course>>(){}.getType(), new Serializer.CourseSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Building>>(){}.getType(), new Serializer.BuildingSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Person>>(){}.getType(), new Serializer.DataSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Venue>>(){}.getType(), new Serializer.VenueSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<BusStop>>(){}.getType(), new Serializer.BusStopSerializer());
            gsonBuilder.registerTypeAdapter(NewDiningHall.class, new Serializer.MenuSerializer());
            gsonBuilder.registerTypeAdapter(BusRoute.class, new Serializer.BusRouteSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<BusRoute>>(){}.getType(), new Serializer.BusRouteListSerializer());
            Gson gson = gsonBuilder.create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint("http://api.pennlabs.org")
                    .build();
            mLabs = restAdapter.create(Labs.class);
        }
        return mLabs;
    }

    public OkHttpClient getAPIClient() {
        if (mAPIClient == null) {
            mAPIClient = new OkHttpClient();
        }
        return mAPIClient;
    }
}
