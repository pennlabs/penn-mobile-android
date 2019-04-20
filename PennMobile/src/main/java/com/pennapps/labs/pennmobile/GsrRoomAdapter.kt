package com.pennapps.labs.pennmobile

import android.content.Context
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.joda.time.DateTime

class GsrRoomAdapter(internal var timeRanges: ArrayList<String>, internal var ids: ArrayList<String>,
                     internal var gsrLocationCode: String, internal var context: Context,
                     internal var startTimes: ArrayList<DateTime>) : RecyclerView.Adapter<GsrRoomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrRoomHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_room, parent, false)
        val gsrRoomHolder = GsrRoomHolder(view)

        //whenever a time slot is clicked, open spinner to let user pick a duration
        gsrRoomHolder.gsrRoom.setOnClickListener {
            val position = it.tag as Int

            gsrRoomHolder.gsrDuration.adapter = ArrayAdapter(context, R.layout.gsr_spinner_item, getValidDurations(position))

            gsrRoomHolder.gsrDuration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posDuration: Int, p3: Long) {
                    // OnItemSelected is called at initialization before the user actually chooses a duration
                    val localGSRID = ids[position]
                    val durationMinutes = (posDuration) * 30
                    if (durationMinutes > 0) {
                        val startTime = startTimes[position].toString()
                        val endTime = startTimes[position].plusMinutes(durationMinutes).toString()
                        Log.d("@@@", "position is $position")
                        if (Integer.parseInt(gsrLocationCode) == 1) {
                            Log.d("@@@@@", "opening huntsmanGSRlogin")
                            val huntsmanGSRLogin = HuntsmanGSRLogin.newInstance(localGSRID, gsrLocationCode, startTime, endTime)
                            val fragmentManager = (context as MainActivity).supportFragmentManager
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, huntsmanGSRLogin)
                                    .addToBackStack("GSR Fragment")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                        } else {
                            val bookGsrFragment = BookGsrFragment.newInstance(localGSRID, gsrLocationCode, startTime, endTime)
                            val fragmentManager = (context as MainActivity).supportFragmentManager
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, bookGsrFragment)
                                    .addToBackStack("GSR Fragment")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                        }
                    }
                }
            }
        }
        return gsrRoomHolder
    }


    override fun onBindViewHolder(holder: GsrRoomHolder, position: Int) {
        if (position < itemCount) {
            holder.gsrRoom.tag = holder.layoutPosition
            val time = timeRanges[position]
            holder.gsrStartTime.text = time.substring(0, time.indexOf("-"))
            holder.gsrEndTime.text = time.substring(time.indexOf("-") + 1)
            holder.gsrId.text = ids[position]
            holder.locationId.text = gsrLocationCode
        }
    }

    private fun getValidDurations(position: Int): Array<String> {
        val durations : ArrayList<String> = arrayListOf("", "30m")
        val time = timeRanges[position]
        val firstEndTime = timeRanges[position].substring(time.indexOf("-") + 1)
        if (position + 1 < itemCount && timeRanges[position + 1].substring(0, time.indexOf("-")) == firstEndTime) {
            durations.add("60m")
            val secondEndTime = timeRanges[position + 1].substring(time.indexOf("-") + 1)
            if (position + 2 < itemCount && timeRanges[position + 2].substring(0, time.indexOf("-")) == secondEndTime) {
                durations.add("90m")
                val thirdEndTime = timeRanges[position + 2].substring(time.indexOf("-") + 1)
                if (Integer.parseInt(gsrLocationCode) != 1 && position + 3 < itemCount && timeRanges[position + 3].substring(0, time.indexOf("-")) == thirdEndTime) {
                    durations.add("120m")
                }
            }
        }
        return durations.toTypedArray()
    }

    override fun getItemCount(): Int {
        return timeRanges.size
    }
}
