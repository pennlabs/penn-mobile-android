package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.AnyRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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

import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private static Labs mLabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mDrawerList.getMenu().findItem(R.id.navHome).setChecked(true);

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
            selectItem(id);
            return false;
        }
    }

    private void selectItem(@AnyRes int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.navHome:
                fragment = new MainFragment();
                break;
            case R.id.navRegistrar:
            case R.id.registrar_img:
            case R.id.registrar_cont:
            case R.id.registrar_button:
                fragment = new RegistrarSearchFragment();
                break;
            case R.id.navDirectory:
            case R.id.directory_img:
            case R.id.directory_cont:
            case R.id.directory_button:
                fragment = new DirectoryFragment();
                break;
            case R.id.navDining:
            case R.id.dining_img:
            case R.id.dining_cont:
            case R.id.dining_button:
                fragment = new DiningFragment();
                break;
            case R.id.navTransit:
            case R.id.transit_img:
            case R.id.transit_cont:
            case R.id.transit_button:
                fragment = new TransitFragment();
                break;
            case R.id.navNews:
            case R.id.news_img:
            case R.id.news_cont:
            case R.id.news_button:
                fragment = new NewsFragment();
                break;
            case R.id.navMap:
            case R.id.map_img:
            case R.id.map_cont:
            case R.id.map_button:
                fragment = new MapFragment();
                break;
            case R.id.navSupport:
                fragment = new SupportFragment();
                break;
            case R.id.navAbout:
                fragment = new AboutFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void onHomeButtonClick(View v) {
        final Menu menu = mDrawerList.getMenu();
        final int id = v.getId();
        if (id == R.id.registrar_img || id == R.id.registrar_cont || id == R.id.registrar_button) {
            menu.findItem(R.id.navRegistrar).setChecked(true);
        } else if (id == R.id.directory_img || id == R.id.directory_cont || id == R.id.directory_button) {
            menu.findItem(R.id.navDirectory).setChecked(true);
        } else if (id == R.id.dining_img || id == R.id.dining_cont || id == R.id.dining_button) {
            menu.findItem(R.id.navDining).setChecked(true);
        } else if (id == R.id.transit_img || id == R.id.transit_cont || id == R.id.transit_button) {
            menu.findItem(R.id.navTransit).setChecked(true);
        } else if (id == R.id.news_img || id == R.id.news_cont || id == R.id.news_button) {
            menu.findItem(R.id.navNews).setChecked(true);
        } else if (id == R.id.map_img || id == R.id.map_cont || id == R.id.map_button) {
            menu.findItem(R.id.navMap).setChecked(true);
        }
        selectItem(id);
    }

    public static Labs getLabsInstance() {
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

    public void showErrorToast(final int errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
