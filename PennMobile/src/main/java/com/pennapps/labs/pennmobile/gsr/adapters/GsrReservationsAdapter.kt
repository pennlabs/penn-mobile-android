package com.pennapps.labs.pennmobile.gsr.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.GsrReservationBinding
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GsrReservationsAdapter(
    private var reservations: ArrayList<GSRReservation>,
) : RecyclerView.Adapter<GsrReservationsAdapter.GsrReservationViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GsrReservationViewHolder {
        mContext = parent.context
        val itemBinding = GsrReservationBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return GsrReservationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: GsrReservationViewHolder,
        position: Int,
    ) {
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
        Picasso
            .get()
            .load(imageUrl)
            .fit()
            .centerCrop()
            .into(holder.gsrReservationIv)

        holder.gsrReservationLocationTv.text = roomName
        holder.gsrReservationDateTv.text = day + "\n" + fromHour + "-" + toHour

        holder.gsrReservationCancelButton.setOnClickListener {
            // create dialog to confirm that you want to cancel reservation
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Are you sure?")
            builder.setMessage("Please confirm that you wish to delete this booking.")

            builder.setPositiveButton("Confirm") { _, _ ->
                val bookingID = reservation.bookingId

                (mContext as MainActivity).mNetworkManager.getAccessToken {
                    val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                    val sessionID =
                        if (reservation.info == null) {
                            sp.getString(
                                mContext.getString(R.string.huntsmanGSR_SessionID),
                                "",
                            )
                        } else {
                            null
                        }

                    (mContext as MainActivity).lifecycleScope.launch {
                        val labs = MainActivity.studentLifeInstance
                        val bearerToken =
                            "Bearer " + sp.getString(mContext.getString(R.string.access_token), " ")

                        try {
                            val response =
                                labs.cancelReservation(
                                    bearerToken,
                                    null,
                                    bookingID,
                                    sessionID,
                                )

                            if (response.isSuccessful) {
                                if (reservations.size > position) {
                                    reservations.removeAt(position)
                                }
                                run {
                                    // Sends request to gsr reservation widget
                                    mContext.sendBroadcast(Intent(GsrReservationWidget.UPDATE_GSR_WIDGET))
                                    if (reservations.size == 0) {
                                        var intent = Intent("refresh")
                                        LocalBroadcastManager
                                            .getInstance(mContext)
                                            .sendBroadcast(intent)
                                    } else {
                                        notifyItemRemoved(position)
                                    }
                                }
                            } else {
                                val error =
                                    Exception(
                                        response.errorBody()?.string()
                                            ?: "Unknown error",
                                    )

                                Log.e(
                                    "GsrReservationsAdapter",
                                    "Error canceling gsr reservation",
                                    error,
                                )
                                Toast
                                    .makeText(
                                        mContext,
                                        "Error deleting your GSR reservation.",
                                        LENGTH_SHORT,
                                    ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            builder.setNegativeButton("Cancel") { _, _ -> }

            builder.show()
        }
    }

    override fun getItemCount(): Int = reservations.size

    inner class GsrReservationViewHolder(
        itemBinding: GsrReservationBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val gsrReservationCancelButton = itemBinding.gsrReservationCancelBtn
        val gsrReservationLocationTv = itemBinding.gsrReservationLocationTv
        val gsrReservationDateTv = itemBinding.gsrReservationDateTv
        val gsrReservationIv = itemBinding.gsrReservationIv
    }
}
