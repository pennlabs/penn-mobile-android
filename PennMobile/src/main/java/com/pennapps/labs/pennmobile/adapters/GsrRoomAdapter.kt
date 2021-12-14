package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.classes.GSRRoom
import org.joda.time.DateTime

class GsrRoomAdapter(internal var timeRanges: ArrayList<String>, internal var ids: ArrayList<String>,
                     internal var gsrLocationCode: String, internal var context: Context,
                     internal var startTimes: ArrayList<DateTime>, internal var duration: Int,
                        internal var gids: ArrayList<Int>, internal var roomNames: ArrayList<String>,
                        internal var starts: ArrayList<String>, internal var ends: ArrayList<String>) : RecyclerView.Adapter<GsrRoomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrRoomHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_room, parent, false)
        val gsrRoomHolder = GsrRoomHolder(view)

        //whenever a time slot is clicked, open spinner to let user pick a duration
        gsrRoomHolder.gsrRoom.setOnClickListener {
            val position = gsrRoomHolder.adapterPosition

            val localGSRID = ids[position]
            val gid = gids[position]
            val roomName = roomNames[position]
            if (duration > 0) {
                val startTime = starts[position]
                val endTime = ends[position]
                if (Integer.parseInt(gsrLocationCode) == 1) {
                    val huntsmanGSRLogin = HuntsmanGSRLogin.newInstance(localGSRID, gsrLocationCode, startTime, endTime,
                        gid, roomName)
                    val fragmentManager = (context as MainActivity).supportFragmentManager
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, huntsmanGSRLogin)
                            .addToBackStack("GSR Fragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                } else {
                    val bookGsrFragment = BookGsrFragment.newInstance(localGSRID, gsrLocationCode, startTime, endTime,
                            gid, localGSRID.toInt(), roomName)
                    val fragmentManager = (context as MainActivity).supportFragmentManager
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, bookGsrFragment)
                            .addToBackStack("GSR Fragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                }
            }
        }
        return gsrRoomHolder
    }


    override fun onBindViewHolder(holder: GsrRoomHolder, position: Int) {
        if (position < itemCount) {
            val time = timeRanges[position]
            holder.gsrStartTime.text = time.substring(0, time.indexOf("-"))
            holder.gsrEndTime.text = time.substring(time.indexOf("-") + 1)
            holder.gsrId.text = ids[position]
            holder.locationId.text = gsrLocationCode
        }
    }

    override fun getItemCount(): Int {
        return timeRanges.size
    }
}
