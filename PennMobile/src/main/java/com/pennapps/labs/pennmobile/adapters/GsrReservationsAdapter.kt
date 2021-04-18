package com.pennapps.labs.pennmobile.adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.LocalDateTime
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response



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
        val localDateTime = LocalDateTime()

        val fromHour24Hours = "18:30"
                //from.toString("HH:mm")
        val reservationDate : Int = 103
                //from.dayOfYear().get()



        val currentDay : Int = localDateTime.dayOfYear().get()
        val currentHour = localDateTime.hourOfDay().get()
        val currentMinute = localDateTime.minuteOfHour().get()

        val currentHoursMinutesPassed = currentHour * 60 + currentMinute
        val reservationHoursMinutesPassed =
                (fromHour24Hours.substringBefore(":").toInt() * 60) +
                        (fromHour24Hours.substringAfter(":").toInt())

        if (currentHoursMinutesPassed > reservationHoursMinutesPassed) {
            val timeDifferenceMin = (24*60) - (currentHoursMinutesPassed - reservationHoursMinutesPassed)
            val dayDifferenceMin = (reservationDate - currentDay - 1) * 24 * 60
            val totalMinutes = timeDifferenceMin + dayDifferenceMin - 10
            alarmManagerSetUp(totalMinutes, 0)
        } else {
            val timeDifferenceMin = (reservationHoursMinutesPassed - currentHoursMinutesPassed)
            val dayDifferenceMin = (reservationDate - currentDay) * 24 * 60
            val totalMinutes = timeDifferenceMin + dayDifferenceMin - 10
            alarmManagerSetUp(totalMinutes, 0)
        }
        //

        /*
        if (currentHour > fromHour){
        //in0between days + part of a new day
            (24 hours * diffence in days) + (currentHour - fromHour * 60 min)
            - (10 minutes for alarm)
        else {
        //last-in between day has not happened, so (inbetween days - 1) + part of last between day
            (24 hours * (diffence in days - 1)) + (fromHour - currentHour * 60 min)

        we can round to days and hours when informing them of the alarm. the actual alarm length
        needs to include minute differences
         */

        //alarmManagerSetUp(0, 0)
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
                val sp = PreferenceManager.getDefaultSharedPreferences(mContext)
                val sessionID = if (reservation.info == null) sp.getString(mContext.getString(R.string.huntsmanGSR_SessionID), "") else null

                val labs = MainActivity.labsInstance
                labs.cancelReservation(null, bookingID, sessionID, object : ResponseCallback() {
                    override fun success(response: Response) {
                        if (reservations.size > position) {
                            reservations.removeAt(position)
                        }
                        run {
                            if (reservations.size == 0) {
                                var intent = Intent("refresh")
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
                            } else {
                                notifyItemRemoved(position)
                            }}
                    }

                    override fun failure(error: RetrofitError) {
                        Toast.makeText(mContext, "Error deleting your GSR reservation.", LENGTH_SHORT).show()
                    }
                })
            }

            builder.setNegativeButton("Cancel") { _, _ -> }

            builder.show()
        }
    }

    private fun alarmManagerSetUp(time : Int, id : Int){
//        val id: Int = (mRoomName + mMachineType).hashCode() + machineId

        val intent = Intent(mContext, GSRBroadcastReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.putExtra(mContext.resources.getString(R.string.laundry_room_name), mRoomName)
//        intent.putExtra(mContext.resources.getString(R.string.laundry_machine_type), mMachineType)
//        intent.putExtra(mContext.resources.getString(R.string.laundry_machine_id), id)


        // switch is off if no alarm

        // switch is off if no alarm
//        if (alarmIntent == null) {
//            mSwitch.setChecked(false)
//        } else {
//            mSwitch.setChecked(true)
//        }
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time * 60000] = alarmIntent


//        mSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
//           // val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//            // checked button
//            if (isChecked) {
//                //val alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//                // for testing 10 second notification
//                //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, alarmIntent);
//
//                // snackbar
//                val stringBuilder = StringBuilder()
//                stringBuilder.append("Alarm set for $time minutes")
//                val snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT)
//                val subView = snackbar.view
//                val snackTextView = subView.findViewById<View>(R.id.snackbar_text) as TextView
//                snackTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white))
//                snackbar.show()
//            } else {
//                // cancel alarm if exists
//                val alarmIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_NO_CREATE)
//                if (alarmIntent != null) {
//                    alarmManager.cancel(alarmIntent)
//                    alarmIntent.cancel()
//                }
//                if (buttonView.context == null) {
//                    return@OnCheckedChangeListener
//                }
//
//                // snackbar
//                val stringBuilder = StringBuilder()
//                stringBuilder.append("Alarm off")
//                if (buttonView != null) {
//                    val snackbar = Snackbar.make(buttonView, stringBuilder, Snackbar.LENGTH_SHORT)
//                    val subView = snackbar.view
//                    val snackTextView = subView.findViewById<TextView>(R.id.snackbar_text)
//                    snackTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white))
//                    snackbar.show()
//                }
//            }
//        })
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class GsrReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
