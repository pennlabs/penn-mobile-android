package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import kotlin.Result.Companion.success
import android.R.attr.radius



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
        val roomName = reservation.name

        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val from = formatter.parseDateTime(reservation.fromDate)
        val to = formatter.parseDateTime(reservation.toDate)
        val day = from.toString("EEEE, MMMM d")
        val fromHour = from.toString("h:mm a")
        val toHour = to.toString("h:mm a")

        val imageUrl = reservation.info?.get("thumbnail") //?: "huntsman_url" TODO: put huntsman image url

        // set image
        Picasso.get().load(imageUrl).fit().centerCrop().into(holder.itemView.gsr_reservation_iv)

        // update ViewHolder
        holder.itemView.gsr_reservation_location_tv.text = roomName
        holder.itemView.gsr_reservation_date_tv.text = day + "\n" + fromHour + "-" + toHour

        holder.itemView.gsr_reservation_cancel_btn.setOnClickListener {
            // create dialog to confirm that you want to cancel reservation
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Are you sure?")
            builder.setMessage("Please confirm that you wish to delete this booking.")

            builder.setPositiveButton("Confirm") { dialog, which ->
                val bookingID = reservation.booking_id
                val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                val sessionid = if (reservation.info == null) sp.getString(mContext.getString(R.string.huntsmanGSR_SessionID), "") else null

                val labs = MainActivity.getLabsInstance()
                labs.cancelReservation(bookingID, sessionid, object : ResponseCallback() {
                    override fun success(response: Response) {
                        Log.d("GsrReservations", response.status.toString())
                    }

                    override fun failure(error: RetrofitError) {
                        Log.d("GsrReservations", error.toString())
                    }
                })
            }

            builder.setNegativeButton("Cancel") { _, _ -> }

            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class GsrReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
