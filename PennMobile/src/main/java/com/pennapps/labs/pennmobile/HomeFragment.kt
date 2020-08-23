package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.home_cells_rv
import kotlinx.android.synthetic.main.fragment_home.home_refresh_layout
import kotlinx.android.synthetic.main.fragment_home.initials
import kotlinx.android.synthetic.main.fragment_home.profile_background
import kotlinx.android.synthetic.main.fragment_home.title_view
import kotlinx.android.synthetic.main.fragment_home.view.appbar_home
import kotlinx.android.synthetic.main.fragment_home.view.date_view
import kotlinx.android.synthetic.main.fragment_home.view.home_cells_rv
import kotlinx.android.synthetic.main.fragment_home.view.home_refresh_layout
import kotlinx.android.synthetic.main.fragment_home.view.profile
import kotlinx.android.synthetic.main.loading_panel.loadingPanel
import java.util.Locale


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

        getHomePage()
        initAppBar(view)
        return view
    }

    private fun getHomePage() {

        // get session id from shared preferences
        val sp = sharedPreferences
        val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
        val accountID = sp.getString(getString(R.string.accountID), "")
        val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()

        // get API data
        val labs = MainActivity.labsInstance
        labs.getHomePage(deviceID, accountID, sessionID).subscribe({ cells ->
            mActivity.runOnUiThread {
                val gsrBookingCell = HomeCell()
                gsrBookingCell.type = "gsr_booking"
                gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "VP Weigle")
                cells?.add(cells.size - 1, gsrBookingCell)
                home_cells_rv?.adapter = HomeAdapter(ArrayList(cells))
                loadingPanel?.visibility = View.GONE
                home_refresh_layout?.isRefreshing = false
                view?.let { displaySnack(it, "Just Updated") }
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                Log.e("Home", "Could not load Home page")
                throwable.printStackTrace()
                Toast.makeText(mActivity, "Could not load Home page", Toast.LENGTH_LONG).show()
                loadingPanel?.visibility = View.GONE
                home_refresh_layout?.isRefreshing = false
            }
        })
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        this.setTitle(getString(R.string.home))
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

        // Appbar behavior init
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams
                as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.profile.setOnClickListener { _ ->
            //TODO: Account Settings
            displaySnack(view, "Meow")
        }

    }

    /**
     * Show a SnackBar message right below the app bar
     */
    @Suppress("DEPRECATION")
    private fun displaySnack(view: View, text: String) {
        (view as ViewGroup).showErrorSneaker(message = text, doOnRetry = { getHomePage() })
    }
}