package com.pennapps.labs.pennmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
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
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.HomeScreenCell;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
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
    private static Labs mLabsHome;
    private static final int CODE_MAP = 1;
    private boolean tab_showed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
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
        ) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList = (NavigationView) findViewById(R.id.navigation);
        mDrawerList.setNavigationItemSelectedListener(new DrawerItemClickListener());
        mDrawerList.getMenu().findItem(R.id.nav_home).setChecked(true);

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
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        try {
            WebView webView = NewsTab.currentWebView;
            if (webView.canGoBack()) {
                webView.goBack();
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        } catch (NullPointerException ignored) {
            // No webview exists currently
            super.onBackPressed();
            if (CourseFragment.containsNum(getTitle())) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                mDrawerToggle.syncState();
            }
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
            removeTabs();
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();
            int id = item.getItemId();
            item.setChecked(true);
            navigateLayout(id);
            return false;
        }
    }

    private void navigateLayout(@AnyRes int id) {

        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragment = new MainFragment();
                }
                break;
            case R.id.nav_registrar:
            case R.id.registrar_cont:
                fragment = new RegistrarFragment();
                break;
            case R.id.nav_gsr:
            case R.id.gsr_cont:
                fragment = new GsrFragment();
                break;
            case R.id.nav_dining:
            case R.id.dining_cont:
                fragment = new DiningFragment();
                break;
            case R.id.nav_directory:
            case R.id.directory_cont:
                fragment = new DirectoryFragment();
                break;
            case R.id.nav_news:
            case R.id.news_cont:
                fragment = new NewsFragment();
                break;
//            case R.id.nav_map:
//            case R.id.map_cont:
//                getPermission();
//                return;
            case R.id.nav_laundry:
            case R.id.laundry_cont:
                Intent intent = new Intent(this, LaundryActivity.class);
                startActivity(intent);
                break;
//            case R.id.nav_fling:
//                fragment = new FlingFragment();
//                break;
            case R.id.nav_nso:
                fragment = new NsoFragment();
                break;
            case R.id.nav_support:
                fragment = new SupportFragment();
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
            case R.id.nav_pref:
                fragment = new PreferenceFragment();
                break;
            case R.id.nav_fitness:
                fragment = new FitnessFragment();
                break;
        }

        fragmentTransact(fragment);
    }

    public void onHomeButtonClick(View v) {
        navigateLayout(v.getId());
    }

    public static Labs getLabsInstance() {
        if (mLabs == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Course>>() {
            }.getType(), new Serializer.CourseSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Building>>() {
            }.getType(), new Serializer.BuildingSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Person>>() {
            }.getType(), new Serializer.DataSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Venue>>() {
            }.getType(), new Serializer.VenueSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<BusStop>>() {
            }.getType(), new Serializer.BusStopSerializer());
            gsonBuilder.registerTypeAdapter(DiningHall.class, new Serializer.MenuSerializer());
            gsonBuilder.registerTypeAdapter(BusRoute.class, new Serializer.BusRouteSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<BusRoute>>() {
            }.getType(), new Serializer.BusRouteListSerializer());
            // gets room
            gsonBuilder.registerTypeAdapter(new TypeToken<LaundryRoom>() {
            }.getType(), new Serializer.LaundryRoomSerializer());
            // gets laundry room list
            gsonBuilder.registerTypeAdapter(new TypeToken<List<LaundryRoomSimple>>() {
            }.getType(), new Serializer.LaundryRoomListSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<GSRLocation>>() {
            }.getType(), new Serializer.GsrLocationSerializer());
            // gets laundry usage
            gsonBuilder.registerTypeAdapter(new TypeToken<LaundryUsage>() {
            }.getType(), new Serializer.LaundryUsageSerializer());
            // gets laundry preferences (used only for testing)
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Integer>>(){
            }.getType(), new Serializer.LaundryPrefSerializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<FlingEvent>>(){
            }.getType(), new Serializer.FlingEventSerializer());
            // gets fitness
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Gym>>(){
            }.getType(), new Serializer.GymSerializer());
            Gson gson = gsonBuilder.create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint("https://api.pennlabs.org")
                    .build();
            mLabs = restAdapter.create(Labs.class);
        }
        return mLabs;
    }

    public static Labs getLabsInstanceHome() {
        if (mLabsHome == null) {
            // homepage endpoint
            GsonBuilder gsonBuilderHomePage = new GsonBuilder();
            gsonBuilderHomePage.registerTypeAdapter(new TypeToken<List<HomeScreenCell>>() {
            }.getType(), new Serializer.HomePageSerializer());
            Gson gsonHome = gsonBuilderHomePage.create();
            RestAdapter restAdapterHome = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gsonHome))
                    .setEndpoint("http://api-dev.pennlabs.org")
                    .build();
            mLabsHome = restAdapterHome.create(Labs.class);
        }
        return mLabsHome;
    }

    public void showErrorToast(final int errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ActionBarDrawerToggle getActionBarToggle() {
        return mDrawerToggle;
    }

    public void setNav(int id) {
        final Menu menu = mDrawerList.getMenu();
        menu.findItem(id).setChecked(true);
    }

    public void addTabs(FragmentStatePagerAdapter pageAdapter, final ViewPager pager, boolean scrollable) {
        if (tab_showed) {
            return;
        }
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);

        final TabLayout tabLayout = (TabLayout) getLayoutInflater().inflate(R.layout.tab_layout, null);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager);
            }
        });
        if (!scrollable) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        appBar.addView(tabLayout, 1);

        pager.setAdapter(pageAdapter);
        tab_showed = true;
    }

    public void removeTabs() {
        tab_showed = false;
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        if (appBar != null && appBar.getChildCount() >= 3) {
            appBar.removeViewAt(1);
        }
    }

    public void openMapDirectionMenu() {
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.getChildAt(0).setVisibility(View.GONE);
        appBar.getChildAt(1).setVisibility(View.VISIBLE);
    }

    public void closeMapDirectionMenu() {
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.getChildAt(0).setVisibility(View.VISIBLE);
        appBar.getChildAt(1).setVisibility(View.GONE);
    }

    public RelativeLayout getMenuMapExtension() {
        return (RelativeLayout) findViewById(R.id.menu_map_extension);
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat
                    .requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, CODE_MAP);
        } else {
            Fragment fragment = new MapFragment();
            fragmentTransact(fragment);
        }
    }

    private void fragmentTransact(Fragment fragment) {
        if (fragment != null) {
            final Fragment frag = fragment;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, frag)
                                .addToBackStack("Main Activity")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    } catch (IllegalStateException e) {
                        //ignore because the onSaveInstanceState etc states are called when activity is going to background etc
                    }
                }
            });
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showErrorToast(R.string.ask_permission_fail);
            return;
        }
        if (requestCode == CODE_MAP) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new MapFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commitAllowingStateLoss();
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            });
        }
    }

    
}
