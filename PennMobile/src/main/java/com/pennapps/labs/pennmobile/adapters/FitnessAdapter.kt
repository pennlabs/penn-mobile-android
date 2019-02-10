package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Gym
import com.pennapps.labs.pennmobile.classes.GymHours
import com.squareup.picasso.Picasso

import org.joda.time.DateTime
import org.joda.time.Interval

import butterknife.BindView
import butterknife.ButterKnife

class FitnessAdapter(context: Context, // gym data
                     private val gyms: List<Gym>)// get gym data from fragment (which gets it from the api call)
    : RecyclerView.Adapter<FitnessAdapter.FitnessViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FitnessViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fitness_list_item, parent, false)
        mContext = parent.context
        return FitnessViewHolder(view)
    }

    override fun onBindViewHolder(holder: FitnessViewHolder, position: Int) {
        val g = gyms[position]
        // get the data from Gym class
        val name = g.name
        val hours = g.hours

        // if the gym is open or not
        val open = g.isOpen
        var openClosed = "OPEN"
        if (!open) {
            openClosed = "CLOSED"
            // set background to red for closed
            holder.gymStatusView!!.setBackgroundResource(R.drawable.label_red)
        }


        // get first word in name
        val i = name!!.indexOf(' ')
        var simpName: String = name
        if (i >= 0) {
            simpName = name.substring(0, i)
        }
        simpName = simpName.toLowerCase()

        // build image resource name from simpName
        val src_name = "gym_$simpName"

        // get resource identifier
        var identifier = mContext!!.resources.getIdentifier(src_name, "drawable", mContext!!.packageName)
        if (identifier == 0) { // if the src name is invalid
            identifier = mContext!!.resources.getIdentifier("gym_fox", "drawable", mContext!!.packageName)
        }

        // set image
        Picasso.get().load(identifier).fit().centerCrop().into(holder.gymImageView)


        // update ViewHolder
        holder.gymNameView!!.text = name
        holder.gymStatusView!!.text = openClosed
        holder.gymHoursView!!.text = intervalsToString(hours)
    }


    // turn list of hours into string
    private fun intervalsToString(hours: List<Interval>): String {
        val i1 = hours[0]
        // first check if it's all day
        if (i1 == GymHours.allDayInterval) {
            return ""
        }
        // otherwise add intervals to String
        val sb = StringBuilder()
        sb.append(intervalToString(i1))
        for (ii in 1 until hours.size) {
            val interval = hours[ii]
            sb.append(" | ")
            sb.append(intervalToString(interval))
        }

        return sb.toString()
    }

    private fun intervalToString(interval: Interval): String {
        val d1 = interval.start
        val d2 = interval.end

        return formatTime(d1) + " - " + formatTime(d2)
    }

    private fun formatTime(time: DateTime): String {
        return if (time.toString("mm") == "00") {
            time.toString("h a")
        } else {
            time.toString("h:mm a")
        }
    }

    override fun getItemCount(): Int {
        return gyms.size
    }

    inner class FitnessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.gym_image_view)
        var gymImageView: ImageView? = null
        @BindView(R.id.gym_name_view)
        var gymNameView: TextView? = null
        @BindView(R.id.gym_status_view)
        var gymStatusView: TextView? = null
        @BindView(R.id.gym_hours_view)
        var gymHoursView: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
