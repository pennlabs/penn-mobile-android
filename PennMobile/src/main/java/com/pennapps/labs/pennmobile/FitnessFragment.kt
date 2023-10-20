package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import kotlinx.android.synthetic.main.fragment_fitness.*
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
        view.gym_list?.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        // handle swipe to refresh
        view.gym_refresh_layout?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.gym_refresh_layout?.setOnRefreshListener { getGymData() }
        // get api data
        getGymData()
    }

    private fun getGymData() {

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionFitness?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_fitness?.text = "Not Connected to Internet"
            internetConnectionFitness?.visibility = View.VISIBLE
        } else {
            internetConnectionFitness?.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.fitness)
        mActivity.setSelectedTab(MainActivity.MORE)
    }
}