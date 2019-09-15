package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.FitnessAdapter
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.classes.GSRReservation
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_fitness.*
import kotlinx.android.synthetic.main.fragment_fitness.view.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*

class GsrReservationsFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("GsrReservations")
                .putContentType("App Feature")
                .putContentId("9"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gsr_reservations, container, false)

        // set layout manager for RecyclerView
        view.gsr_reservations_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        // add divider
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        view.gsr_reservations_rv.addItemDecoration(divider)

//        var reservations: MutableList<GSRReservation> = mutableListOf()
//        for (i in 0..7) {
//            reservations.add(GSRReservation())
//        }
//
//        view.gsr_reservations_rv.adapter = GsrReservationsAdapter(reservations)

        // handle swipe to refresh
         view.gsr_reservations_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
         view.gsr_reservations_refresh_layout.setOnRefreshListener { getReservations() }

        // get api data
        getReservations()

        return view
    }

    private fun getReservations() {
        // get API data
        val labs = MainActivity.getLabsInstance() //TODO: store user email and fetch for that user
        labs.getGsrReservations("martagf@seas.upenn.edu").subscribe({ reservations ->
            mActivity.runOnUiThread {
                gsr_reservations_rv.adapter = GsrReservationsAdapter(reservations)
                // get rid of loading screen
//                loadingPanel.visibility = View.GONE
//                if (reservations.size > 0) {
//                    no_results.visibility = View.GONE
//                }
                // stop refreshing
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {
                    // no need to do anything, we've just moved away from this activity
                }
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load GSR reservations", Toast.LENGTH_LONG).show()
                // get rid of loading screen
//                loadingPanel.visibility = View.GONE
//                // display no results
//                no_results.visibility = View.VISIBLE
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {
                    // no need to do anything, we've just moved away from this activity
                }
            }
        })
    }

}