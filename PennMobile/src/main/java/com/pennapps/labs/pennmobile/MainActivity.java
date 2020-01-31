package com.pennapps.labs.pennmobile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pennapps.labs.pennmobile.ExpandedBottomNavBar.ExpandableBottomTabBar;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.api.PennInTouchNetworkManager;
import com.pennapps.labs.pennmobile.api.Platform;
import com.pennapps.labs.pennmobile.api.Serializer;
import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.HomeCell;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.User;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit.ResponseCallback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity {

    private static Labs mLabs;
    private static Platform mPlatform;
    private static final int CODE_MAP = 1;
    private ExpandableBottomTabBar tabBarView;
    private boolean tab_showed;
    private boolean loggedIn;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        if (Build.VERSION.SDK_INT > 28){
            setTheme(R.style.DarkModeApi29);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Set default fragment to HomeFragment
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new HomeFragment());
        tx.commit();

        // Expandable bottom nav bar
        tabBarView = findViewById(R.id.bottom_navigation);
        onExpandableBottomNavigationItemSelected();

        // TODO: testing account methods, put in the right place
        mPlatform = getPlatformInstance();
        mPlatform.getAccessToken("test_auth_code", "",
                "https://pennlabs.org/pennmobile/android/callback/", "",
                new ResponseCallback() {
            @Override
            public void success(Response response) {
                Log.d("Accounts", "access token: " + response.getBody());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Accounts", "Error fetching access token " + error);
            }
        });

        mPlatform.getUser("test_access_token", new ResponseCallback() {
            @Override
            public void success(Response response) {
                Log.d("Accounts", "user: " + response.getBody());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Accounts", "Error getting user " + error);
            }
        });

    }

    private void onExpandableBottomNavigationItemSelected() {
        tabBarView.setOnTabClickedListener(new ExpandableBottomTabBar.OnTabClickedListener() {
            @Override
            public void onTabClicked(View view, int tabPos) {
                Fragment fragment = null;
                switch (tabPos) {
                    case 0:
                        if (MainActivity.this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            fragment = new HomeFragment();
                        }
                        break;
                    case 1:
                        fragment = new GsrTabbedFragment();
                        break;
                    case 2:
                        fragment = new DiningFragment();
                        break;
                    case 3:
                        fragment = new LaundryFragment();
                        break;
                    case 5:
                        fragment = new FitnessFragment();
                        break;
                    case 6:
                        fragment = new RegistrarFragment();
                        break;
                    case 7:
                        fragment = new DirectoryFragment();
                        break;
                    case 8:
                        fragment = new NewsFragment();
                        break;
                    case 9:
                        fragment = new FlingFragment();
                        break;
                    case 10:
                        fragment = new SupportFragment();
                        break;
                    case 11:
                        fragment = new SettingsFragment();
                        break;
                    case 12:
                        fragment = new AboutFragment();
                        break;
                }

                MainActivity.this.fragmentTransact(fragment);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.main_title);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setSelectedTab(int index) {
        tabBarView.resetFocusOnAllTabs();
        tabBarView.setSelectedTab(index);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static Platform getPlatformInstance() {
        if (mPlatform == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint("https://platform.pennlabs.org")
                    .build();
            mPlatform = restAdapter.create(Platform.class);
        }
        return mPlatform;
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
            // gets gsr reservations
            gsonBuilder.registerTypeAdapter(new TypeToken<List<GSRReservation>>(){
            }.getType(), new Serializer.GsrReservationSerializer());
            // gets homepage
            gsonBuilder.registerTypeAdapter(new TypeToken<List<HomeCell>>(){
            }.getType(), new Serializer.HomePageSerializer());
            //get user
            gsonBuilder.registerTypeAdapter(User.class, new Serializer.UserSerializer());
            Gson gson = gsonBuilder.create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(new GsonConverter(gson))
                    .setEndpoint("https://api.pennlabs.org")
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            switch (requestCode) {
                case SaveContactsFragment.permission_read:
                    showErrorToast(R.string.ask_contacts_fail);
                    break;
                default:
                    showErrorToast(R.string.ask_location_fail);
                    break;
            }
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
                }
            });
        }
    }
    
}
