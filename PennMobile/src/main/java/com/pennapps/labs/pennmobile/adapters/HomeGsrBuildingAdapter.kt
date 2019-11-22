package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import kotlinx.android.synthetic.main.home_gsr_building.view.*
import kotlinx.android.synthetic.main.university_event.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class HomeGsrBuildingAdapter(private var buildings: ArrayList<String>)
    : RecyclerView.Adapter<HomeGsrBuildingAdapter.HomeGsrBuildingViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeGsrBuildingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_gsr_building, parent, false)
        mContext = parent.context
        return HomeGsrBuildingViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeGsrBuildingViewHolder, position: Int) {
        val building = buildings[position]

        holder.itemView.home_gsr_building_tv.text = building
        holder.itemView.home_gsr_building_iv
        if (building == "Huntsman Hall") {
            holder.itemView.home_gsr_building_iv.setImageResource(R.drawable.huntsman)
        } else {
            holder.itemView.home_gsr_building_iv.setImageResource(R.drawable.weigle)
        }
        holder.itemView.setOnClickListener {
            // TODO: go to gsr fragment with this location
        }



    }

    override fun getItemCount(): Int {
        return buildings.size
    }

    inner class HomeGsrBuildingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}