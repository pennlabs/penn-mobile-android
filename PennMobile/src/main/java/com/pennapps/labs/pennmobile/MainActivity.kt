package com.pennapps.labs.pennmobile

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.ExpandedBottomNavBar.ExpandableBottomTabBar
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.Serializer.*
import com.pennapps.labs.pennmobile.classes.*
import retrofit.RestAdapter
import retrofit.android.AndroidLog
import retrofit.converter.GsonConverter

class MainActivity : AppCompatActivity() {

    private var tabBarView: ExpandableBottomTabBar? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private var tabShowed = false
    private lateinit var fragmentManager: FragmentManager
    private lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        if (Build.VERSION.SDK_INT > 28) {
            setTheme(R.style.DarkModeApi29)
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Android version")
            alert.setMessage("You are running an older version of Android. Features may be limited")
            alert.setPositiveButton("OK", null)
            alert.show()
        }
        super.onCreate(savedInstanceState)
        if (applicationContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.DarkBackground)
        }
        setContentView(R.layout.activity_main)
        tabBarView = findViewById(R.id.bottom_navigation)
        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Show HomeFragment if logged in, otherwise show LoginFragment
        val pennkey = mSharedPrefs.getString(getString(R.string.pennkey), null)
        val guestMode = mSharedPrefs.getBoolean(getString(R.string.guest_mode), false)
        if (pennkey == null && !guestMode) {
            startLoginFragment()
        } else {
            startHomeFragment()
        }
    }

    private fun onExpandableBottomNavigationItemSelected() {
        tabBarView?.setOnTabClickedListener { view, tabPos ->
            var fragment: Fragment? = null
            when (tabPos) {
                HOME -> if (fragmentManager.backStackEntryCount > 0) {
                    fragment = HomeFragment()
                }
                GSR -> fragment = GsrTabbedFragment()
                DINING -> fragment = DiningFragment()
                LAUNDRY -> fragment = LaundryFragment()
                FITNESS -> fragment = FitnessFragment()
                NEWS -> fragment = NewsFragment()
                SUPPORT -> fragment = SupportFragment()
                SETTINGS -> fragment = SettingsFragment()
                ABOUT -> fragment = AboutFragment()
            }
            fragmentTransact(fragment)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setSelectedTab(index: Int) {
        tabBarView?.resetFocusOnAllTabs()
        tabBarView?.selectedTab = index
    }

    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun startHomeFragment() {
        val fragment: Fragment = HomeFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        onExpandableBottomNavigationItemSelected()
        toolbar?.visibility = View.VISIBLE
        tabBarView?.visibility = View.VISIBLE
    }

    fun startLoginFragment() {
        val fragment: Fragment = LoginFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        toolbar?.visibility = View.GONE
        tabBarView?.visibility = View.GONE
    }

    fun showErrorToast(errorMessage: Int) {
        runOnUiThread { Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show() }
    }

    fun addTabs(pageAdapter: FragmentStatePagerAdapter?, pager: ViewPager, scrollable: Boolean) {
        if (tabShowed) {
            return
        }
        val appBar = findViewById<View>(R.id.appbar) as AppBarLayout
        val tabLayout = layoutInflater.inflate(R.layout.tab_layout, null) as TabLayout
        tabLayout.post { tabLayout.setupWithViewPager(pager) }
        if (!scrollable) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        }
        appBar.addView(tabLayout, 1)
        pager.adapter = pageAdapter
        tabShowed = true
    }

    fun removeTabs() {
        tabShowed = false
        val appBar = findViewById<View>(R.id.appbar) as AppBarLayout

        if (appBar.childCount >= 2) {
            appBar.removeViewAt(1)
        }
    }

    fun fragmentTransact(fragment: Fragment?) {
        if (fragment != null) {
            runOnUiThread {
                try {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                } catch (e: IllegalStateException) {
                    //ignore because the onSaveInstanceState etc states are called when activity is going to background etc
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (requestCode == SaveContactsFragment.permission_read) {
                showErrorToast(R.string.ask_contacts_fail)
            }
            return
        }
    }

    override fun setTitle(title: CharSequence) {
        toolbarTitle?.text = title
    }

    companion object {
        // 4 corresponds to the More button
        const val HOME = 0
        const val GSR = 1
        const val DINING = 2
        const val LAUNDRY = 3
        const val FITNESS = 5
        const val NEWS = 6
        const val SUPPORT = 7
        const val SETTINGS = 8
        const val ABOUT = 9

        private var mLabs: Labs? = null
        private var mPlatform: Platform? = null

        @JvmStatic
        val platformInstance: Platform
            get() {
                if (mPlatform == null) {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    val restAdapter = RestAdapter.Builder()
                            .setConverter(GsonConverter(gson))
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setLog(AndroidLog("Platform"))
                            .setEndpoint(Platform.platformBaseUrl)
                            .build()
                    mPlatform = restAdapter.create(Platform::class.java)
                }
                return mPlatform!!
            }

        @JvmStatic
        val labsInstance: Labs
            get() {
                if (mLabs == null) {
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<Contact?>?>() {}.type, DataSerializer<Any?>())
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<Venue?>?>() {}.type, VenueSerializer())
                    gsonBuilder.registerTypeAdapter(DiningHall::class.java, MenuSerializer())
                    // gets room
                    gsonBuilder.registerTypeAdapter(object : TypeToken<LaundryRoom?>() {}.type, LaundryRoomSerializer())
                    // gets laundry room list
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<LaundryRoomSimple?>?>() {}.type, LaundryRoomListSerializer())
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<GSRLocation?>?>() {}.type, GsrLocationSerializer())
                    // gets laundry usage
                    gsonBuilder.registerTypeAdapter(object : TypeToken<LaundryUsage?>() {}.type, LaundryUsageSerializer())
                    // gets laundry preferences (used only for testing)
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<Int?>?>() {}.type, LaundryPrefSerializer())
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<FlingEvent?>?>() {}.type, FlingEventSerializer())
                    // gets fitness
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<Gym?>?>() {}.type, GymSerializer())
                    // gets gsr reservations
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<GSRReservation?>?>() {}.type, GsrReservationSerializer())
                    // gets homepage
                    gsonBuilder.registerTypeAdapter(object : TypeToken<MutableList<HomeCell?>?>() {}.type, HomePageSerializer())
                    // gets user
                    gsonBuilder.registerTypeAdapter(Account::class.java, UserSerializer())
                    val gson = gsonBuilder.create()
                    val restAdapter = RestAdapter.Builder()
                            .setConverter(GsonConverter(gson))
                            .setEndpoint("https://api.pennlabs.org")
                            .build()
                    mLabs = restAdapter.create(Labs::class.java)
                }
                return mLabs!!
            }
    }

}

//checks if internet is connected
fun isOnline(context: Context?): Boolean {
    val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    } else {
                        return true
                    }
                } else {
                    return true
                }
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
    }
    return false
}