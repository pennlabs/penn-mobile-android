package com.pennapps.labs.pennmobile

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.classes.GSRContainer
import org.joda.time.DateTime
import java.util.*

class GsrBuildingAdapter(internal var context: Context, internal var gsrs: ArrayList<GSRContainer>,
                         internal var gsrLocationCode: String) : RecyclerView.Adapter<GsrBuildingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrBuildingHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gsr_building, parent, false)
        return GsrBuildingHolder(view)
    }

    override fun onBindViewHolder(holder: GsrBuildingHolder, position: Int) {
        if (position < itemCount) {
            val gsrRoomsRecyclerView = holder.recyclerView
            if (gsrRoomsRecyclerView != null) {
                val gsrRoomsLayoutManager = LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false)
                gsrRoomsRecyclerView.layoutManager = gsrRoomsLayoutManager

                //now define arrays
                val timeRanges = ArrayList<String>()
                val startTimes = ArrayList<DateTime>()
                val ids = ArrayList<String>()

                for (j in 0 until gsrs[position].availableGSRSlots.size) {
                    val gsrslot = gsrs[position].availableGSRSlots[j]
                    timeRanges.add(gsrslot.timeRange)
                    startTimes.add(gsrslot.startTime)
                    ids.add(gsrslot.elementId)
                }
                gsrRoomsRecyclerView.adapter = GsrRoomAdapter(timeRanges, ids, gsrLocationCode, context, startTimes)
                holder.gsrBuildingName?.text = gsrs[position].gsrName
            }
        }
    }

    override fun getItemCount(): Int { return gsrs.size }
}