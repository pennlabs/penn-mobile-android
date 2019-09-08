package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.classes.GSRReservation
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*

class GsrReservationsFragment : Fragment() {

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

        var reservations: MutableList<GSRReservation> = mutableListOf()
        for (i in 0..7) {
            reservations.add(GSRReservation())
        }

        view.gsr_reservations_rv.adapter = GsrReservationsAdapter(reservations)

        // handle swipe to refresh
        // view.gsr_reservations_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        // view.gym_refresh_layout.setOnRefreshListener { getGymData() }
        // TODO: fetch reservations

        return view
    }

}