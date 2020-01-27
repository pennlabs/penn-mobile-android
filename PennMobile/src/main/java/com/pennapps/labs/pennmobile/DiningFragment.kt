package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.DiningAdapter
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.Venue
import kotlinx.android.synthetic.main.fragment_dining.*
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*
import rx.Observable
import rx.functions.Action1
import rx.functions.Func1

class DiningFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mLabs: Labs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

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
        val v = inflater.inflate(R.layout.fragment_dining, container, false)
        v.dining_swiperefresh?.setOnRefreshListener { getDiningHalls() }
        v.dining_swiperefresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        v.dining_halls_recycler_view?.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        val divider = DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL)
        v.dining_halls_recycler_view?.addItemDecoration(divider)
        getDiningHalls()
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dining_sort, menu)
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        // sort the dining halls in the user-specified order
        val order = sp.getString("dining_sortBy", "RESIDENTIAL")
        if (order == "RESIDENTIAL") {
            menu.findItem(R.id.action_sort_residential).isChecked = true
        } else if (order == "NAME") {
            menu.findItem(R.id.action_sort_name).isChecked = true
        } else {
            menu.findItem(R.id.action_sort_open).isChecked = true
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
                    }
                }, {
                    mActivity.runOnUiThread {
                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.VISIBLE
                        dining_swiperefresh?.isRefreshing = false
                    }
                })
    }

    // Takes a venue then adds an image and modifies venue name if name is too long
    private fun createHall(venue: Venue): DiningHall {
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

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.dining)
        if (Build.VERSION.SDK_INT > 17) {
            mActivity.setSelectedTab(2)
        }
    }
}
