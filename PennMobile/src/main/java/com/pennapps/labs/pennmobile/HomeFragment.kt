package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.renderscript.RenderScript
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.collapsingtoolbar.behavior.ImageHelper
import com.pennapps.labs.pennmobile.collapsingtoolbar.behavior.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlin.math.abs


class HomeFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager
                .getInstance(mActivity)
                .registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "11")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.home_cells_rv.layoutManager = LinearLayoutManager(context,
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
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
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
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val firstName = sp.getString(getString(R.string.first_name), null)
        val initials = sp.getString(getString(R.string.initials), null)
        if (firstName != null) {
            this.setTitle("Welcome, $firstName!")
        } else {
            this.setTitle(getString(R.string.main_title))
        }
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.profile_background.setImageDrawable(resources.getDrawable
                (R.drawable.ic_guest_avatar, context?.theme))
            } else {
                @Suppress("DEPRECATION")
                this.profile_background.setImageDrawable(resources.getDrawable
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
        // Appbar behavior init
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams
                    as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.profile.setOnClickListener { _ ->
            displaySnack(view, "Meow")
        }

    }

    /**
     * Show a SnackBar message right below the app bar
     */
    @Suppress("DEPRECATION")
    private fun displaySnack(view: View, text: String) {
        val snackBar = Snackbar.make(view.snack_bar_location, text, Snackbar.LENGTH_SHORT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBar.setTextColor(resources.getColor(R.color.white, context?.theme))
            snackBar.setBackgroundTint(resources.getColor(R.color.penn_mobile_grey, context?.theme))
        } else {
            snackBar.setTextColor(resources.getColor(R.color.white))
            snackBar.setBackgroundTint(resources.getColor(R.color.penn_mobile_grey))
        }

        // SnackBar message and action TextViews are placed inside a LinearLayout
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        // Flips the orientation
        for (i in 0 until snackBarLayout.childCount) {
            val parent = snackBarLayout.getChildAt(i)
            if (parent is LinearLayout) {
                parent.rotation = 180F
                break
            }
        }
        snackBar.show()
    }
}