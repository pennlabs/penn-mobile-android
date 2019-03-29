package com.pennapps.labs.pennmobile

import android.content.Context
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GsrRoomAdapter(internal var times: ArrayList<String>, internal var ids: ArrayList<String>,
                     internal var gsrLocationCode: String, internal var context: Context,
                     internal var dates: ArrayList<String>) : RecyclerView.Adapter<GsrRoomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrRoomHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_room, parent, false)
        return GsrRoomHolder(view)
    }


    override fun onBindViewHolder(holder: GsrRoomHolder, position: Int) {
        if (position < getItemCount()) {

            val localGSRID = ids[position]
            val time = times[position]
            val date = dates[position]
            val startTime = transformStartTime(time, date)
            val endTime = transformEndTime(time, date)
            holder.gsrStartTime.text = time.substring(0, time.indexOf("-"))
            holder.gsrEndTime.text = time.substring(time.indexOf("-") + 1)
            holder.gsrId.text = ids[position]
            holder.locationId.text = gsrLocationCode
            //whenever a time slot is clicked

            holder.gsrRoom.setOnClickListener {
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

    internal fun transformStartTime(inputTime: String, date: String): String {
        var start = inputTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        start = convertToMilitaryTime(start)
        start = start.replace(":", "")

        val c = Calendar.getInstance()

        //date is numerical number of our date
        if (Integer.parseInt(date) < c.get(Calendar.DAY_OF_MONTH)) {
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1)
        }
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date))


        val df = SimpleDateFormat("yyyyMMdd")
        val formattedDate = df.format(c.time)
        return formattedDate + "T" + start + currentTimeZoneOffset
    }

    internal fun transformEndTime(inputTime: String, date: String): String {
        var end = inputTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        end = convertToMilitaryTime(end)
        end = end.replace(":", "")

        val c = Calendar.getInstance()

        //date is numerical number of our date
        if (Integer.parseInt(date) < c.get(Calendar.DAY_OF_MONTH)) {
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1)
        }
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date))

        val df = SimpleDateFormat("yyyyMMdd")
        val formattedDate = df.format(c.time)
        return formattedDate + "T" + end + currentTimeZoneOffset
    }

    //helper function that turns military to civilian time
    fun convertToMilitaryTime(input: String): String {
        val militaryTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val civilianTimeFormatter = DateTimeFormat.forPattern("hh:mm a")
        return militaryTimeFormatter.print(civilianTimeFormatter.parseDateTime(input))
    }

    override fun getItemCount(): Int {
        return times.size
    }

    companion object {

        val currentTimeZoneOffset: String
            get() {
                val tz = DateTimeZone.forID("America/New_York")
                val instant = DateTime.now().millis

                val name = tz.getName(instant)

                val offsetInMilliseconds = tz.getOffset(instant).toLong()
                val hours = TimeUnit.MILLISECONDS.toHours(offsetInMilliseconds)


                return "00-0" + Integer.toString(Math.abs(hours).toInt()) + "00"
            }
    }
}
