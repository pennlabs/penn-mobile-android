package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.DiningAdapter
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_dining.*
import kotlinx.android.synthetic.main.fragment_dining.title_view
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_dining.view.appbar_home
import kotlinx.android.synthetic.main.fragment_dining.view.profile
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*
import rx.Observable

class DiningFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mLabs: Labs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.labsInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        setHasOptionsMenu(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Dining")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dining, container, false)
        view.dining_swiperefresh?.setOnRefreshListener { getDiningHalls() }
        view.dining_swiperefresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.dining_halls_recycler_view?.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        getDiningHalls()
        initAppBar(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Dining")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dining_sort, menu)
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        // sort the dining halls in the user-specified order
        when (sp.getString("dining_sortBy", "RESIDENTIAL")) {
            "RESIDENTIAL" -> {
                menu.findItem(R.id.action_sort_residential).isChecked = true
            }
            "NAME" -> {
                menu.findItem(R.id.action_sort_name).isChecked = true
            }
            else -> {
                menu.findItem(R.id.action_sort_open).isChecked = true
            }
        }
        val diningInfoFragment = fragmentManager?.findFragmentByTag("DINING_INFO_FRAGMENT")
        menu.setGroupVisible(R.id.action_sort_by, diningInfoFragment == null || !diningInfoFragment.isVisible)
    }

    private fun setSortByMethod(method: String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sp.edit()
        editor.putString("dining_sortBy", method)
        editor.apply()
        getDiningHalls()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            android.R.id.home -> {
                mActivity.onBackPressed()
                return true
            }
            R.id.action_sort_open -> {
                setSortByMethod("OPEN")
                item.isChecked = true
                return true
            }
            R.id.action_sort_residential -> {
                setSortByMethod("RESIDENTIAL")
                item.isChecked = true
                return true
            }
            R.id.action_sort_name -> {
                setSortByMethod("NAME")
                item.isChecked = true
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun getDiningHalls() {
        // Map each item in the list of venues to a Venue Observable, then map each Venue to a DiningHall Observable
        mLabs.venues()
                .flatMap { venues -> Observable.from(venues) }
                .flatMap { venue ->
                    val hall = createHall(venue)
                    Observable.just(hall)
                }
                .toList()
                .subscribe({ diningHalls ->
                    mActivity.runOnUiThread {
                        val adapter = DiningAdapter(diningHalls)
                        dining_halls_recycler_view?.adapter = adapter
                        loadingPanel?.visibility = View.GONE
                        if (diningHalls.size > 0) {
                            no_results?.visibility = View.GONE
                        }
                        dining_swiperefresh?.isRefreshing = false
                        view?.let { displaySnack(it, "Just Updated") }
                    }
                }, {
                    mActivity.runOnUiThread {
                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.VISIBLE
                        dining_swiperefresh?.isRefreshing = false
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.dining)
        if (Build.VERSION.SDK_INT > 17) {
            mActivity.setSelectedTab(MainActivity.DINING)
        }
    }

    private fun initAppBar(view: View) {
        // Appbar behavior init
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.profile.setOnClickListener { _ ->
            displaySnack(view, "Meow")
        }
    }

    private fun setTitle(title: CharSequence) {
        title_view.text = title
    }


    /**
     * Shows SnackBar message right below the app bar
     */
    @Suppress("DEPRECATION")
    private fun displaySnack(view: View, text: String) {
        val snackBar = Snackbar.make(view.snack_bar_dining, text, Snackbar.LENGTH_SHORT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBar.setTextColor(resources.getColor(R.color.white, context?.theme))
            snackBar.setBackgroundTint(resources.getColor(R.color.penn_mobile_grey, context?.theme))
        } else {
            snackBar.setTextColor(resources.getColor(R.color.white))
            snackBar.setBackgroundTint(resources.getColor(R.color.penn_mobile_grey))
        }
        // SnackBar message and action TextViews are placed inside a LinearLayout
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        for (i in 0 until snackBarLayout.childCount) {
            val parent = snackBarLayout.getChildAt(i)
            if (parent is LinearLayout) {
                parent.rotation = 180F
                break
            }
        }
        snackBar.show()
    }

    companion object {
        // Takes a venue then adds an image and modifies venue name if name is too long
        fun createHall(venue: Venue): DiningHall {
            when (venue.id) {
                593 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
                636 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_hill_house)
                637 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_kceh)
                638 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_hillel)
                639 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_houston)
                640 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_marks)
                641 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_accenture)
                642 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_joes_cafe)
                1442 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_nch)
                747 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_mcclelland)
                1057 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_gourmet_grocer)
                1058 -> return DiningHall(venue.id, "Tortas Frontera", venue.isResidential, venue.getHours(), venue, R.drawable.dining_tortas)
                1163 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
                1731 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_nch)
                1732 -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_mba_cafe)
                1733 -> return DiningHall(venue.id, "Pret a Manger Locust", venue.isResidential, venue.getHours(), venue, R.drawable.dining_pret_a_manger)
                else -> return DiningHall(venue.id, venue.name, venue.isResidential, venue.getHours(), venue, R.drawable.dining_commons)
            }
        }
    }
}
