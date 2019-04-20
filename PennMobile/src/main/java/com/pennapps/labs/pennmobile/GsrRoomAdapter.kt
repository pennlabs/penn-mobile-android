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
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GsrRoomAdapter(internal var times: ArrayList<String>, internal var ids: ArrayList<String>,
                     internal var gsrLocationCode: String, internal var context: Context,
                     internal var dates: ArrayList<String>) : RecyclerView.Adapter<GsrRoomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrRoomHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_room, parent, false)
        val gsrRoomHolder = GsrRoomHolder(view)

        //whenever a time slot is clicked, open spinner to let user pick a duration
        gsrRoomHolder.gsrRoom.setOnClickListener {
            val position = view.tag as Int

            gsrRoomHolder.gsrDuration.adapter = ArrayAdapter(context, R.layout.gsr_spinner_item, getValidDurations(position))

            gsrRoomHolder.gsrDuration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posDuration: Int, p3: Long) {
                    // OnItemSelected is called at initialization before the user actually chooses a duration
                    val localGSRID = ids[position]
                    val time = times[position]
                    val date = dates[position]
                    val durationMinutes = (posDuration) * 30
                    if (durationMinutes > 0) {
                        val startTime = transformStartTime(time, date)
                        val format = DateTimeFormat.forPattern("YYYYMMdd'T'HHmmssZ")
                        val endTime = format.parseDateTime(startTime).plusMinutes(durationMinutes).toString()
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
            holder.gsrRoom.tag = holder.adapterPosition
            val time = times[position]
            holder.gsrStartTime.text = time.substring(0, time.indexOf("-"))
            holder.gsrEndTime.text = time.substring(time.indexOf("-") + 1)
            holder.gsrId.text = ids[position]
            holder.locationId.text = gsrLocationCode
        }
    }

    private fun getValidDurations(position: Int): Array<String> {
        val durations : ArrayList<String> = arrayListOf("", "30m")
        val time = times[position]
        val firstEndTime = times[position].substring(time.indexOf("-") + 1)
        if (position + 1 < itemCount && times[position + 1].substring(0, time.indexOf("-")) == firstEndTime) {
            durations.add("60m")
            val secondEndTime = times[position + 1].substring(time.indexOf("-") + 1)
            if (position + 2 < itemCount && times[position + 2].substring(0, time.indexOf("-")) == secondEndTime) {
                durations.add("90m")
                val thirdEndTime = times[position + 2].substring(time.indexOf("-") + 1)
                if (Integer.parseInt(gsrLocationCode) != 1 && position + 2 < itemCount && times[position + 2].substring(0, time.indexOf("-")) == thirdEndTime) {
                    durations.add("120m")
                }
            }
        }
        return durations.toTypedArray()
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
