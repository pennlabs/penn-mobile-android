package com.pennapps.labs.pennmobile

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.adapters.GSRBroadcastReceiver
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import kotlinx.android.synthetic.main.gsr_details_book.view.*
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


class BookGsrFragment : Fragment() {

    // fields for booking
    internal lateinit var firstNameEt: EditText
    internal lateinit var lastNameEt: EditText
    internal lateinit var emailEt: EditText
    // submit button
    private lateinit var submit: Button

    private lateinit var mLabs: Labs
    private lateinit var mActivity: Activity

    // gsr details
    private lateinit var gsrID: String
    private lateinit var gsrLocationCode: String
    private lateinit var startTime: String
    private lateinit var endTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {arguments ->
            gsrID = arguments.getString("gsrID") ?: ""
            gsrLocationCode = arguments.getString("gsrLocationCode") ?: ""
            startTime = arguments.getString("startTime") ?: ""
            endTime = arguments.getString("endTime") ?: ""
        }
        mLabs = MainActivity.labsInstance
        mActivity = activity as MainActivity
        mActivity?.setTitle(R.string.gsr)
    }

    override fun onResume() {
        super.onResume()
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.setTitle(R.string.gsr)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.gsr_details_book, container, false)

        firstNameEt = v.first_name
        lastNameEt = v.last_name
        emailEt = v.gsr_email
        submit = v.submit_gsr

        // get user email and name from shared preferences if it's already saved
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val email = sp.getString(getString(R.string.email_address), "")
        val firstName = sp.getString(getString(R.string.first_name), "")
        val lastName = sp.getString(getString(R.string.last_name), "")

        firstNameEt.setText(firstName)
        lastNameEt.setText(lastName)
        emailEt.setText(email)

        submit.setOnClickListener {
            if (firstNameEt.text.toString().matches("".toRegex()) || lastNameEt.text.toString().matches("".toRegex())
                    || emailEt.text.toString().matches("".toRegex())) {
                Toast.makeText(activity, "Please fill in all fields before booking",
                        Toast.LENGTH_LONG).show()
            } else if (!emailEt.text.toString().matches("""[\w]+@(seas\.|sas\.|wharton\.|nursing\.)?upenn\.edu""".toRegex())) {
                Toast.makeText(activity, "Please enter a valid Penn email", Toast.LENGTH_LONG).show()
            } else {
                bookGSR(Integer.parseInt(gsrID), Integer.parseInt(gsrLocationCode), startTime, endTime)

            }
        }
        return v
    }

    private fun bookGSR(gsrId: Int, gsrLocationCode: Int, startTime: String?, endTime: String?) {

        //setting notification 10 mins before startTime
        if (startTime != null) {
            Log.d("GSR", startTime)
        }
        val from = LocalDateTime.parse(startTime)
        val fromHour24Hours = from.toString("HH:mm")
        val reservationDate = from.dayOfYear().get()
        val localDateTime = LocalDateTime()
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



        var sessionID = ""
        activity?.let { activity ->
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
        }

        mLabs.bookGSR(
                //Passing the values
                sessionID,
                gsrLocationCode,
                gsrId,
                startTime,
                endTime,
                firstNameEt.text.toString(),
                lastNameEt.text.toString(),
                emailEt.text.toString(),
                "Penn Mobile GSR",
                "2158986533",
                "2-3",

                //Creating an anonymous callback
                object : Callback<GSRBookingResult> {
                    override fun success(result: GSRBookingResult, response: Response) {
                        //Displaying the output as a toast and go back to GSR fragment
                        if (result.getResults() == true) {
                            Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()

                            // Save user info in shared preferences
                            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                            val editor = sp.edit()
                            editor.putString(getString(R.string.first_name), firstNameEt.text.toString())
                            editor.putString(getString(R.string.last_name), lastNameEt.text.toString())
                            editor.putString(getString(R.string.email_address), emailEt.text.toString())
                            editor.apply()
                        }
                        else {
                            Toast.makeText(activity, "GSR booking failed", Toast.LENGTH_LONG).show()
                            Log.e("BookGsrFragment", "GSR booking failed with " + result.getError())
                        }
                        // go back to GSR fragment
                        val fragmentManager = (context as MainActivity).supportFragmentManager
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, GsrTabbedFragment())
                                .addToBackStack("GSR Fragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()
                    }

                    override fun failure(error: RetrofitError) {
                        //If any error occurred displaying the error as toast
                        Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                        val fragmentManager = (context as MainActivity).supportFragmentManager
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, GsrTabbedFragment())
                                .addToBackStack("GSR Fragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()
                    }
                }
        )
    }

    companion object {

        fun newInstance(gsrID: String, gsrLocationCode: String, startTime: String, endTime: String): BookGsrFragment {
            val fragment = BookGsrFragment()
            val args = Bundle()
            args.putString("gsrID", gsrID)
            args.putString("gsrLocationCode", gsrLocationCode)
            args.putString("startTime", startTime)
            args.putString("endTime", endTime)
            fragment.arguments = args
            return fragment
        }
    }

    //set notification with specific time
    private fun alarmManagerSetUp(time : Int, id : Int){
//        val id: Int = (mRoomName + mMachineType).hashCode() + machineId
        val theContext = mActivity.applicationContext

        val intent = Intent(theContext, GSRBroadcastReceiver::class.java)
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
        val alarmManager = theContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = PendingIntent.getBroadcast(theContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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

}