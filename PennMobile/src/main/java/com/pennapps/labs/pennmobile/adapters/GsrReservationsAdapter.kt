package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.widget.Toast.LENGTH_SHORT
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager


class GsrReservationsAdapter(private var reservations: ArrayList<GSRReservation>)
    : RecyclerView.Adapter<GsrReservationsAdapter.GsrReservationViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_reservation, parent, false)
        mContext = parent.context
        return GsrReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GsrReservationViewHolder, position: Int) {
        val reservation = reservations[position]

        val roomName = reservation.name

        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val from = formatter.parseDateTime(reservation.fromDate)
        val to = formatter.parseDateTime(reservation.toDate)
        val day = from.toString("EEEE, MMMM d")
        val fromHour = from.toString("h:mm a")
        val toHour = to.toString("h:mm a")

        // huntsman reservation responses don't have an image url so we set it here
        val imageUrl = reservation.info?.get("thumbnail") ?: "https://s3.us-east-2.amazonaws.com/labs.api/dining/MBA+Cafe.jpg"
        Picasso.get().load(imageUrl).fit().centerCrop().into(holder.itemView.gsr_reservation_iv)

        holder.itemView.gsr_reservation_location_tv.text = roomName
        holder.itemView.gsr_reservation_date_tv.text = day + "\n" + fromHour + "-" + toHour

        holder.itemView.gsr_reservation_cancel_btn.setOnClickListener {
            // create dialog to confirm that you want to cancel reservation
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Are you sure?")
            builder.setMessage("Please confirm that you wish to delete this booking.")

            builder.setPositiveButton("Confirm") { _, _ ->
                val bookingID = reservation.booking_id

                (mContext as MainActivity).mNetworkManager.getAccessToken {

                    val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                    val sessionID = if (reservation.info == null) sp.getString(
                        mContext.getString(R.string.huntsmanGSR_SessionID),
                        ""
                    ) else null

                    val labs = MainActivity.studentLifeInstance
                    val bearerToken =
                        "Bearer " + sp.getString(mContext.getString(R.string.access_token), " ")
                    labs.cancelReservation(
                        bearerToken,
                        null,
                        bookingID,
                        sessionID,
                        object : ResponseCallback() {
                            override fun success(response: Response) {
                                if (reservations.size > position) {
                                    reservations.removeAt(position)
                                }
                                run {
                                    if (reservations.size == 0) {
                                        var intent = Intent("refresh")
                                        LocalBroadcastManager.getInstance(mContext)
                                            .sendBroadcast(intent)
                                    } else {
                                        notifyItemRemoved(position)
                                    }
                                }
                            }

                            override fun failure(error: RetrofitError) {
                                Log.e(
                                    "GsrReservationsAdapter",
                                    "Error canceling gsr reservation",
                                    error
                                )
                                Toast.makeText(
                                    mContext,
                                    "Error deleting your GSR reservation.",
                                    LENGTH_SHORT
                                ).show()
                            }
                        })
                }
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
