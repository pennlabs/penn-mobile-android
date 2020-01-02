package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import kotlinx.android.synthetic.main.fragment_fitness.*
import kotlinx.android.synthetic.main.fragment_fitness.view.*
import kotlinx.android.synthetic.main.fragment_fitness.view.gym_list
import kotlinx.android.synthetic.main.fragment_fitness.view.gym_refresh_layout
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*

class FitnessFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "9")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Fitness")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fitness, container, false)


        // set layout manager for RecyclerView
        view.gym_list.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        // add divider
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        view.gym_list.addItemDecoration(divider)

        // handle swipe to refresh
        view.gym_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.gym_refresh_layout.setOnRefreshListener { getGymData() }
        // get api data
        getGymData()
    }

    private fun getGymData() {
        // get API data
        val labs = MainActivity.getLabsInstance()
        labs.gymData.subscribe({ gyms ->
            mActivity.runOnUiThread {
                gym_list.adapter = FitnessAdapter(gyms)
                // get rid of loading screen
                loadingPanel.visibility = View.GONE
                if (gyms.size > 0) {
                    no_results.visibility = View.GONE
                }
                // stop refreshing
                try {
                    gym_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {
                    // no need to do anything, we've just moved away from this activity
                }
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load gym information", Toast.LENGTH_LONG).show()
                // get rid of loading screen
                loadingPanel.visibility = View.GONE
                // display no results
                no_results.visibility = View.VISIBLE
                try {
                    gym_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {
                    // no need to do anything, we've just moved away from this activity
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.fitness)
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(5)
        }
    }

    companion object {

        fun newInstance(): FitnessFragment {
            return FitnessFragment()
        }
    }
}