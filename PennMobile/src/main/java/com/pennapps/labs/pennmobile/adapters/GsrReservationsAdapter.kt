package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import org.joda.time.DateTime

class GsrReservationsAdapter(private val reservations: List<GSRReservation>)// get reservations data from fragment
    : RecyclerView.Adapter<GsrReservationsAdapter.GsrReservationViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_reservation, parent, false)
        mContext = parent.context
        return GsrReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GsrReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        // get the data from GsrReservation class
        val roomName = reservation.room_name
        val date = reservation.date
        val imageUrl = reservation.image_url

        // set image
//        Picasso.get().load(imageUrl).fit().centerCrop().into(holder.itemView.gym_image_view)
//
//        // update ViewHolder
//        holder.itemView.gsr_reservation_location_tv.text = roomName
//        holder.itemView.gsr_reservation_date_tv.text = date
    }

    private fun formatTime(time: DateTime): String {
        return if (time.toString("mm") == "00") {
            time.toString("h a")
        } else {
            time.toString("h:mm a")
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class GsrReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
