package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_fitness.*
import kotlinx.android.synthetic.main.fragment_fitness.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*


class FitnessFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Fitness")
                .putContentType("App Feature")
                .putContentId("9"))
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
        view.gym_refresh_layout.setOnRefreshListener { getGymData() }

        // get api data
        getGymData()


        return view
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
        mActivity.setTitle(R.string.fitness)
        mActivity.setNav(R.id.nav_fitness)
    }

    companion object {

        fun newInstance(): FitnessFragment {
            return FitnessFragment()
        }
    }
}
