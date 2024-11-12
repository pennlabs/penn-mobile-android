package com.pennapps.labs.pennmobile

import StudentLifeRf2
import android.Manifest
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
import android.webkit.CookieManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.classes.Account
import com.pennapps.labs.pennmobile.api.fragments.LoginFragment
import com.pennapps.labs.pennmobile.components.sneaker.Sneaker
import com.pennapps.labs.pennmobile.databinding.ActivityMainBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.Venue
import com.pennapps.labs.pennmobile.fling.classes.FlingEvent
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.home.classes.Post
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.more.classes.Contact
import com.pennapps.labs.pennmobile.more.fragments.SaveContactsFragment
import com.pennapps.labs.pennmobile.utils.Utils
import eightbitlab.com.blurview.BlurView
import kotlinx.coroutines.sync.Mutex
import okhttp3.OkHttpClient
import retrofit.RestAdapter
import retrofit.android.AndroidLog
import retrofit.client.OkClient
import retrofit.converter.GsonConverter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import com.squareup.okhttp.OkHttpClient as SquareOkHttpClient

class MainActivity : AppCompatActivity() {
    private var tabShowed = false
    private lateinit var fragmentManager: FragmentManager
    private lateinit var mSharedPrefs: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    val tokenMutex = Mutex()
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    val mNetworkManager by lazy { OAuth2NetworkManager(this) }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // TODO: Inform user that that your app will not show notifications.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT > 28) {
            setTheme(R.style.DarkModeApi29)
        }
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (applicationContext.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        ) {
            setTheme(R.style.DarkBackground)
        }
        Utils.getCurrentSystemTime()
        askNotificationPermission()

        setSupportActionBar(binding.include.toolbar)
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        val policy =
            StrictMode.ThreadPolicy
                .Builder()
                .permitAll()
                .build()
        StrictMode.setThreadPolicy(policy)

        onExpandableBottomNavigationItemSelected()
        showBottomBar()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.logEvent("MainActivityStart", null)

        // Show HomeFragment if logged in, otherwise show LoginFragment
        val pennKey = mSharedPrefs.getString(getString(R.string.pennkey), null)
        val guestMode = mSharedPrefs.getBoolean(getString(R.string.guest_mode), false)
        if (pennKey == null && !guestMode) {
            startLoginFragment()
        } else {
            startHomeFragment()
        }

        // Did diningWidgetIntentSetup not as separate function as for some reason when
        // diningWidgetBroadcast out of onCreate setTab does not trigger.
        var diningWidgetBroadCast = 0
        if (intent != null) {
            diningWidgetBroadCast = intent.getIntExtra("Widget_Tab_Switch", -1)
        }
        if (diningWidgetBroadCast != -1) {
            setTab(DINING_ID)
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun onExpandableBottomNavigationItemSelected() {
        binding.include.expandableBottomBar.setOnNavigationItemSelectedListener { item ->
            val position =
                when (item.itemId) {
                    R.id.nav_home -> MainPagerAdapter.HOME_POSITION
                    R.id.nav_dining -> MainPagerAdapter.DINING_POSITION
                    R.id.nav_gsr -> MainPagerAdapter.GSR_POSITION
                    R.id.nav_laundry -> MainPagerAdapter.LAUNDRY_POSITION
                    R.id.nav_more -> MainPagerAdapter.MORE_POSITION
                    else -> MainPagerAdapter.HOME_POSITION
                }
            binding.include.mainViewPager.setCurrentItem(position, false)
            true
        }
    }

    fun setTab(id: Int) {
        binding.include.expandableBottomBar.selectedItemId = id
    }

    fun setSelectedTab(id: Int) {}

    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun startHomeFragment() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        val mainPagerAdapter = MainPagerAdapter(fragmentManager, lifecycle)
        binding.include.mainViewPager.isSaveEnabled = false
        binding.include.mainViewPager.adapter = mainPagerAdapter
        binding.include.mainViewPager.isUserInputEnabled = false
        binding.include.mainViewPager.offscreenPageLimit = 5
        binding.include.mainViewPager.visibility = View.VISIBLE
        binding.include.expandableBottomBar.visibility = View.VISIBLE
        setTab(HOME_ID)
    }

    fun startLoginFragment() {
        CookieManager.getInstance().removeAllCookie()
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.remove(getString(R.string.penn_password))
        editor.remove(getString(R.string.penn_user))
        editor.remove(getString(R.string.first_name))
        editor.remove(getString(R.string.last_name))
        editor.remove(getString(R.string.email_address))
        editor.remove(getString(R.string.pennkey))
        editor.remove(getString(R.string.accountID))
        editor.remove(getString(R.string.access_token))
        editor.remove(getString(R.string.guest_mode))
        editor.remove(getString(R.string.initials))
        editor.apply()
        val currentFragment = fragmentManager.findFragmentById(R.id.content_frame)
        val fragment: Fragment = LoginFragment()

        // change the fragment only if we're not already on the login fragment
        if (currentFragment == null || currentFragment::class != fragment::class) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            binding.include.mainViewPager.visibility = View.GONE
            binding.include.expandableBottomBar.visibility = View.GONE
        }
    }

    fun showErrorToast(errorMessage: Int) {
        runOnUiThread { Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show() }
    }

    fun addTabs(
        pageAdapter: FragmentStatePagerAdapter?,
        pager: ViewPager,
        scrollable: Boolean,
    ) {
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

    fun fragmentTransact(
        fragment: Fragment?,
        popBackStack: Boolean,
    ) {
        if (fragment != null) {
            runOnUiThread {
                try {
                    if (popBackStack) {
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit()
                } catch (e: IllegalStateException) {
                    // ignore because the onSaveInstanceState etc states are called when activity is going to background etc
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (requestCode == SaveContactsFragment.PERMISSION_READ) {
                showErrorToast(R.string.ask_contacts_fail)
            }
            return
        }
    }

    override fun setTitle(title: CharSequence) {
        binding.include.appbar
            .findViewById<View>(R.id.toolbar)
            .findViewById<TextView>(R.id.toolbar_title)
            .text = title
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showBottomBar()
    }

    fun hideBottomBar() {
        binding.include.expandableBottomBar.visibility = View.GONE
        val layoutParams = binding.include.contentFrame.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        binding.include.contentFrame.layoutParams = layoutParams
    }

    fun showBottomBar() {
        binding.include.expandableBottomBar.visibility = View.VISIBLE
        val layoutParams = binding.include.contentFrame.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, Utils.dpToPixel(this, 16f))
        binding.include.contentFrame.layoutParams = layoutParams
    }

    companion object {
        const val HOME = 1
        const val GSR = 2
        const val DINING = 3
        const val LAUNDRY = 4
        const val MORE = 5
        const val PCA = 6

        val HOME_ID = R.id.nav_home
        val GSR_ID = R.id.nav_gsr
        val DINING_ID = R.id.nav_dining

        private var mStudentLife: StudentLife? = null
        private var mStudentLifeRf2: StudentLifeRf2? = null
        private var mPlatform: Platform? = null
        private var mCampusExpress: CampusExpress? = null

        @JvmStatic
        val campusExpressInstance: CampusExpress
            get() {
                if (mCampusExpress == null) {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    val restAdapter =
                        RestAdapter
                            .Builder()
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
                    val restAdapter =
                        RestAdapter
                            .Builder()
                            .setConverter(GsonConverter(gson))
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setLog(AndroidLog("Platform"))
                            .setEndpoint(Platform.platformBaseUrl)
                            .build()
                    mPlatform = restAdapter.create(Platform::class.java)
                }
                return mPlatform!!
            }

        val studentLifeInstanceRf2: StudentLifeRf2
            get() {
                if (mStudentLifeRf2 == null) {
                    val okHttpClient =
                        OkHttpClient
                            .Builder()
                            .connectTimeout(35, TimeUnit.SECONDS)
                            .readTimeout(35, TimeUnit.SECONDS)
                            .writeTimeout(35, TimeUnit.SECONDS)
                            .build()

                    val retrofit =
                        Retrofit
                            .Builder()
                            .baseUrl("https://pennmobile.org/api/")
                            .client(okHttpClient)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                    mStudentLifeRf2 = retrofit.create(StudentLifeRf2::class.java)
                }
                return mStudentLifeRf2!!
            }

        @JvmStatic
        val studentLifeInstance: StudentLife
            get() {
                if (mStudentLife == null) {
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Contact?>?>() {}.type,
                        Serializer.DataSerializer<Any?>(),
                    )
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Venue?>?>() {}.type,
                        Serializer.VenueSerializer(),
                    )
                    gsonBuilder.registerTypeAdapter(
                        DiningHall::class.java,
                        Serializer.MenuSerializer(),
                    )
                    // gets room
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<LaundryRoom?>() {}.type,
                        Serializer.LaundryRoomSerializer(),
                    )
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<GSRLocation?>?>() {}.type,
                        Serializer.GsrLocationSerializer(),
                    )
                    // gets laundry preferences (used only for testing)
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Int?>?>() {}.type,
                        Serializer.LaundryPrefSerializer(),
                    )
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<FlingEvent?>?>() {}.type,
                        Serializer.FlingEventSerializer(),
                    )
                    // gets gsr reservations
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<GSRReservation?>?>() {}.type,
                        Serializer.GsrReservationSerializer(),
                    )
                    // gets user
                    gsonBuilder.registerTypeAdapter(
                        Account::class.java,
                        Serializer.UserSerializer(),
                    )
                    // gets posts
                    gsonBuilder.registerTypeAdapter(
                        object : TypeToken<MutableList<Post?>?>() {}.type,
                        Serializer.PostsSerializer(),
                    )
                    val gson = gsonBuilder.create()
                    val okHttpClient = SquareOkHttpClient()
                    okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS) // Connection timeout
                    okHttpClient.setReadTimeout(35, TimeUnit.SECONDS) // Read timeout
                    okHttpClient.setWriteTimeout(35, TimeUnit.SECONDS) // Write timeout
                    val restAdapter =
                        RestAdapter
                            .Builder()
                            .setConverter(GsonConverter(gson))
                            .setClient(OkClient(okHttpClient))
                            .setEndpoint("https://pennmobile.org/api")
                            .build()
                    mStudentLife = restAdapter.create(StudentLife::class.java)
                }
                return mStudentLife!!
            }
    }
}

// checks if internet is connected
fun isOnline(context: Context?): Boolean {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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
    return false
}

/** Shows an error sneaker given a view group with an optional retry function */
fun ViewGroup.showSneakerToast(
    message: String,
    doOnRetry: (() -> Unit)?,
    sneakerColor: Int,
) {
    val sneaker = Sneaker.with(this)
    val view =
        LayoutInflater
            .from(this.context)
            .inflate(R.layout.custom_sneaker_view, sneaker.getView(), false)

    val blurView: BlurView = view.findViewById(R.id.blurView)

    blurView
        .setupWith(this)
        .setFrameClearDrawable(ColorDrawable(Color.TRANSPARENT))
        .setBlurRadius(10f)
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
