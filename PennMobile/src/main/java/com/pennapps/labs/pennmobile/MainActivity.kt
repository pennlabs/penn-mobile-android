package com.pennapps.labs.pennmobile

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.marginBottom
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
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.Serializer.*
import com.pennapps.labs.pennmobile.classes.*
import com.pennapps.labs.pennmobile.components.floatingbottombar.ExpandableBottomBarMenuItem
import com.pennapps.labs.pennmobile.components.sneaker.Sneaker
import com.pennapps.labs.pennmobile.more.MoreFragment
import com.pennapps.labs.pennmobile.utils.Utils
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.custom_sneaker_view.view.*
import kotlinx.android.synthetic.main.include_main.*
import retrofit.RestAdapter
import retrofit.android.AndroidLog
import retrofit.converter.GsonConverter

class MainActivity : AppCompatActivity() {
    private var tabShowed = false
    private lateinit var fragmentManager: FragmentManager
    private lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        if (Build.VERSION.SDK_INT > 28) {
            setTheme(R.style.DarkModeApi29)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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
        Utils.getCurrentSystemTime()

        //tabBarView = findViewById(R.id.bottom_navigation)
       // toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(appbar.findViewById(R.id.toolbar))
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        expandable_bottom_bar.addItems(
                ExpandableBottomBarMenuItem.Builder(this)
                        .addItem(HOME, R.drawable.ic_home_grey)
                        .textRes(R.string.floating_bottom_bar_home)
                        .colorRes(R.color.floating_bottom_bar_selected).create()
                        .addItem(DINING, R.drawable.ic_dining_grey)
                        .textRes(R.string.floating_bottom_bar_dining)
                        .colorRes(R.color.floating_bottom_bar_selected).create()
                        .addItem(GSR, R.drawable.ic_gsr_grey)
                        .textRes(R.string.floating_bottom_bar_gsr_booking)
                        .colorRes(R.color.floating_bottom_bar_selected).create()
                        .addItem(LAUNDRY, R.drawable.ic_laundry_grey)
                        .textRes(R.string.floating_bottom_bar_laundry)
                        .colorRes(R.color.floating_bottom_bar_selected).create()
                        .addItem(MORE, R.drawable.ic_more_grey)
                        .textRes(R.string.floating_bottom_bar_more)
                        .colorRes(R.color.floating_bottom_bar_selected).create()
                        .build()
        )
        onExpandableBottomNavigationItemSelected()
        showBottomBar()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Show HomeFragment if logged in, otherwise show LoginFragment
        val pennKey = mSharedPrefs.getString(getString(R.string.pennkey), null)
        val guestMode = mSharedPrefs.getBoolean(getString(R.string.guest_mode), false)
        if (pennKey == null && !guestMode) {
            startLoginFragment()
        } else {
            startHomeFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomBar()
    }

    private fun onExpandableBottomNavigationItemSelected() {
        expandable_bottom_bar.onItemSelectedListener = { _, item ->
            var fragment: Fragment? = null
            when (item.text as String) {
                "Home" -> if (fragmentManager.backStackEntryCount > 0) {
                    fragment = HomeFragment()
                }
                "Dining" -> fragment = DiningHolderFragment()
                "GSR" -> fragment = GsrTabbedFragment()
                "Laundry" -> fragment = LaundryFragment()
                "More" -> fragment = MoreFragment()
            }
            fragmentTransact(fragment, true)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setSelectedTab(id: Int) {
        expandable_bottom_bar.select(id)
    }

    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun startHomeFragment() {
        OAuth2NetworkManager(this).getAccessToken()
        val fragment: Fragment = HomeFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        expandable_bottom_bar.select(HOME)
        expandable_bottom_bar.visibility = View.VISIBLE
    }

    fun startLoginFragment() {
        val fragment: Fragment = LoginFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        expandable_bottom_bar.visibility = View.GONE
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

    fun fragmentTransact(fragment: Fragment?, popBackStack: Boolean) {
        if (fragment != null) {
            runOnUiThread {
                try {
                    //TODO ALI COMMENT
                    if (popBackStack) {
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_NONE)
                            .commit()
                } catch (e: IllegalStateException) {
                    //ignore because the onSaveInstanceState etc states are called when activity is going to background etc
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (requestCode == SaveContactsFragment.permission_read) {
                showErrorToast(R.string.ask_contacts_fail)
            }
            return
        }
    }

    override fun setTitle(title: CharSequence) {
        appbar.findViewById<View>(R.id.toolbar)
                .findViewById<TextView>(R.id.toolbar_title).text = title
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showBottomBar()
    }

    fun hideBottomBar() {
        expandable_bottom_bar.visibility = View.GONE
        val layoutParams = this.content_frame.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        this.content_frame.layoutParams = layoutParams
    }

    fun showBottomBar() {
        expandable_bottom_bar.visibility = View.VISIBLE
        val layoutParams = this.content_frame.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, Utils.dpToPixel(this, 16f))
        this.content_frame.layoutParams = layoutParams
    }

    companion object {
        const val HOME = 1
        const val GSR = 2
        const val DINING = 3
        const val LAUNDRY = 4
        const val MORE = 5

        private var mStudentLife: StudentLife? = null
        private var mPlatform: Platform? = null
        private var mCampusExpress: CampusExpress? = null

        @JvmStatic
        val campusExpressInstance: CampusExpress
            get() {
                if (mCampusExpress == null) {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    val restAdapter = RestAdapter.Builder()
                        .setConverter(GsonConverter(gson))
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(AndroidLog("Campus Express"))
                        .setEndpoint(Platform.campusExpressBaseUrl)
                        .build()
                    mCampusExpress = restAdapter.create(CampusExpress::class.java)
                }
                return mCampusExpress!!
            }

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
        val studentLifeInstance: StudentLife
            get() {
                if (mStudentLife == null) {
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
                            .setEndpoint("https://pennmobile.org/api")
                            .build()
                    mStudentLife = restAdapter.create(StudentLife::class.java)
                }
                return mStudentLife!!
            }
    }

}

//checks if internet is connected
fun isOnline(context: Context?): Boolean {
    val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

/** Shows an error sneaker given a view group with an optional retry function */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun ViewGroup.showSneakerToast(message: String, doOnRetry: (() -> Unit)?, sneakerColor: Int) {
    val sneaker = Sneaker.with(this)
    val view = LayoutInflater.from(this.context)
            .inflate(R.layout.custom_sneaker_view, sneaker.getView(), false)

    view.blurView.setupWith(this)
            .setFrameClearDrawable(ColorDrawable(Color.TRANSPARENT))
            .setBlurAlgorithm(RenderScriptBlur(this.context))
            .setBlurRadius(10f)
            .setHasFixedTransformationMatrix(true)
            .setOverlayColor(resources.getColor(sneakerColor))

    val retryBtn = view.findViewById<TextView>(R.id.retryButton)
    doOnRetry ?: run { retryBtn.visibility = View.GONE }
    retryBtn.setOnClickListener { doOnRetry?.invoke() }

    val messageView = view.findViewById<TextView>(R.id.errorMessage)
    if (ColorUtils.calculateLuminance(resources.getColor(sneakerColor)) > 0.4) {
        messageView.setTextColor(Color.BLACK)
        view.findViewById<TextView>(R.id.retryButton).setTextColor(Color.BLACK)
    }
    messageView.text = message

    sneaker.sneakCustom(view).setCornerRadius(12, 16).setMessage(message)
}