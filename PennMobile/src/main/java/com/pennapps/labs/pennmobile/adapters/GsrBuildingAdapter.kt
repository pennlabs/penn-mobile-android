package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.GsrBuildingHolder
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRContainer
import org.joda.time.DateTime
import java.util.*

class GsrBuildingAdapter(internal var context: Context, internal var gsrs: ArrayList<GSRContainer>,
                         internal var gsrLocationCode: String, internal var duration: Int) : RecyclerView.Adapter<GsrBuildingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrBuildingHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gsr_building, parent, false)
        return GsrBuildingHolder(view)
    }

    // TODO: what actually calls this building adapter, where is the gsrs arraylist from, so I can presort it before passed into the adapter

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
                val starts = ArrayList<String>()
                val ends = ArrayList<String>()
                val ids = ArrayList<String>()
                val gids = ArrayList<Int>()
                val roomNames = ArrayList<String>()

                // gsrs[position] is a certain gsr room within the building
                // availableGSRSlots are the timeslots within the location and they have all of these associated properties to them

                for (j in 0 until gsrs[position].availableGSRSlots.size) {
                    val gsrslot = gsrs[position].availableGSRSlots[j]
                    timeRanges.add(gsrslot.timeRange)
                    startTimes.add(gsrslot.startTime)
                    starts.add(gsrslot.start)
                    ends.add(gsrslot.end)
                    ids.add(gsrslot.elementId)
                    gids.add(gsrslot.gid)
                    roomNames.add(gsrslot.roomName)
                }

                // within the availableGSRSlots we can sort them by their startTime

                // Add GSR as parameter
                gsrRoomsRecyclerView.adapter = GsrRoomAdapter(timeRanges, ids, gsrLocationCode, context, startTimes, duration, gids, roomNames, starts, ends)
                holder.gsrBuildingName?.text = gsrs[position].gsrName
            }
        }
    }

    override fun getItemCount(): Int { return gsrs.size }
}