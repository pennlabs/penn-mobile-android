package com.pennapps.labs.pennmobile

import android.content.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.DiningHallPreference
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.classes.HomeCellInfo
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        LocalBroadcastManager
            .getInstance(mActivity)
            .registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "11")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.home_cells_rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout
            .setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout
            .setOnRefreshListener { getHomePage() }

        initAppBar(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHomePage()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getHomePage() {

        // get session id from shared preferences
        val sp = sharedPreferences
        val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
        val accountID = sp.getString(getString(R.string.accountID), "")
        val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
        OAuth2NetworkManager(mActivity).getAccessToken()
        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
        Log.i("HomeFragment", bearerToken)

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionHome?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message?.text = getString(R.string.internet_error)
            home_cells_rv?.setPadding(0, 90, 0, 0)
            internetConnectionHome?.visibility = View.VISIBLE
            home_refresh_layout?.isRefreshing = false
            loadingPanel?.visibility = View.GONE
            return
        } else {
            internetConnectionHome?.visibility = View.GONE
            home_cells_rv?.setPadding(0, 0, 0, 0)
        }

        // get API data
        val homepageCells = mutableListOf<HomeCell>()

        for (i in 1..5) {
            homepageCells.add(HomeCell())
        }

        val studentLife = MainActivity.studentLifeInstance
        if (bearerToken != "Bearer ") {

            studentLife.getNews().subscribe({ article ->
                mActivity.runOnUiThread {
                    val newsCell = HomeCell()
                    newsCell.info = HomeCellInfo()
                    newsCell.info?.article = article
                    newsCell.type = "news"
                    homepageCells.set(1, newsCell)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load news", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

            studentLife.getDiningPreferences(bearerToken).subscribe({ preferences ->
                mActivity.runOnUiThread {
                    val list = preferences.preferences
                    val venues = mutableListOf<Int>()
                    val diningCell = HomeCell()
                    diningCell.type = "dining"
                    val diningCellInfo = HomeCellInfo()
                    if(list?.isEmpty() == true ) {
                        venues.add(593)
                        venues.add(1442)
                        venues.add(636)
                    } else {
                        list?.forEach({
                            it?.id?.let { it1 -> venues.add(it1) }
                        })

                    }
                    diningCellInfo.venues = venues
                    diningCell.info = diningCellInfo
                    homepageCells.set(2, diningCell)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    internetConnectionHome?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load Home page", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

            studentLife.getCalendar().subscribe({ events ->
                mActivity.runOnUiThread {
                    val calendar = HomeCell()
                    calendar.type = "calendar"
                    calendar.events = events
                    homepageCells.set(0, calendar)
                    val gsrBookingCell = HomeCell()
                    gsrBookingCell.type = "gsr_booking"
                    gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "Weigle")
                    homepageCells.set(3, gsrBookingCell)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load calendar", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

            studentLife.getLaundryPref(bearerToken).subscribe({ preferences ->
                mActivity.runOnUiThread {
                    val venues = mutableListOf<Int>()
                    val laundryCell = HomeCell()
                    laundryCell.type = "laundry"
                    val laundryCellInfo = HomeCellInfo()
                    if(preferences?.isEmpty() == false ) {
                        laundryCellInfo.roomId = preferences[0]
                    }
                    laundryCell.info = laundryCellInfo
                    homepageCells.set(4, laundryCell)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    internetConnectionHome?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load laundry", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

            /*studentLife.getHomePage(bearerToken).subscribe({ cells ->
                mActivity.runOnUiThread {
                    val gsrBookingCell = HomeCell()
                    gsrBookingCell.type = "gsr_booking"
                    gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "Weigle")
                    cells?.add(cells.size - 1, gsrBookingCell)
                    homepageCells.addAll(homepageCells.size, cells)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    //(home_cells_rv?.adapter as HomeAdapter).notifyDataSetChanged()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load Home page", throwable)
                    throwable.printStackTrace()
                    Toast.makeText(mActivity, "Could not load Home page", Toast.LENGTH_LONG).show()
                    loadingPanel?.visibility = View.GONE
                    internetConnectionHome?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
                    internetConnection_message?.text = getString(R.string.internet_error)
                    internetConnectionHome?.visibility = View.VISIBLE
                    home_refresh_layout?.isRefreshing = false
                }
            }) */
    } else {
            studentLife.getCalendar().subscribe({ events ->
                mActivity.runOnUiThread {
                    val calendar = HomeCell()
                    calendar.type = "calendar"
                    calendar.events = events
                    homepageCells.add(0, calendar)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load Home page", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

            studentLife.getNews().subscribe({ article ->
                mActivity.runOnUiThread {
                    val newsCell = HomeCell()
                    newsCell.info = HomeCellInfo()
                    newsCell.info?.article = article
                    newsCell.type = "news"
                    homepageCells.add(homepageCells.size, newsCell)
                    val gsrBookingCell = HomeCell()
                    gsrBookingCell.type = "gsr_booking"
                    gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "Weigle")
                    homepageCells.add(homepageCells.size, gsrBookingCell)
                    home_cells_rv?.adapter = HomeAdapter(ArrayList(homepageCells))
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("Home", "Could not load Home page", throwable)
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    home_refresh_layout?.isRefreshing = false
                }
            })

    }

    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        this.setTitle(getString(R.string.home))
        mActivity.toolbar.visibility = View.GONE
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.profile_background.setImageDrawable(
                    resources.getDrawable
                    (R.drawable.ic_guest_avatar, context?.theme))
            } else {
                @Suppress("DEPRECATION")
                this.profile_background.setImageDrawable(
                    resources.getDrawable
                    (R.drawable.ic_guest_avatar))
            }
        }
        if (Build.VERSION.SDK_INT > 17) {
            mActivity.setSelectedTab(MainActivity.HOME)
        }
        mActivity.showBottomBar()
    }

    private fun setTitle(title: CharSequence) {
        title_view.text = title
    }

    private fun initAppBar(view: View) {
        val firstName = sharedPreferences.getString(getString(R.string.first_name), null)
        firstName?.let {
            view.date_view.text = "Welcome, $it!".toUpperCase(Locale.getDefault())
            Handler().postDelayed(
                {
                    view.date_view.text = Utils.getCurrentSystemTime()
                },
                4000
            )
        } ?: run {
            view.date_view.text = Utils.getCurrentSystemTime()
        }
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams
                as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.profile.setOnClickListener {
            //TODO: Account Settings
        }
    }

    /**
     * Show a SnackBar message right below the app bar
     */
    @Suppress("DEPRECATION")
    private fun displaySnack(view: View, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            (view as ViewGroup).showSneakerToast(message = text, doOnRetry = { }, sneakerColor = R.color.sneakerBlurColorOverlay)
        }
    }

}