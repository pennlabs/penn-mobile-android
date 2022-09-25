package com.pennapps.labs.pennmobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import kotlinx.android.synthetic.main.gsr_details_book.view.*
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

    private lateinit var mStudentLife: StudentLife

    // gsr details
    private lateinit var gsrID: String
    private lateinit var gsrLocationCode: String
    private lateinit var startTime: String
    private lateinit var endTime: String
    private var gid: Int = 0
    private var roomId: Int = 0
    private lateinit var roomName: String

    //notifications
    val CHANNEL_ID = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {arguments ->
            gsrID = arguments.getString("gsrID") ?: ""
            gsrLocationCode = arguments.getString("gsrLocationCode") ?: ""
            startTime = arguments.getString("startTime") ?: ""
            endTime = arguments.getString("endTime") ?: ""
            gid = arguments.getInt("gid")
            roomId = arguments.getInt("id")
            roomName = arguments.getString("roomName") ?: ""
        }
        mStudentLife = MainActivity.studentLifeInstance
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.setTitle(R.string.gsr)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
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
                bookGSR(Integer.parseInt(gsrID), Integer.parseInt(gsrLocationCode), startTime, endTime, gid, roomId, roomName)
            }
        }
        return v
    }

    private fun bookGSR(gsrId: Int, gsrLocationCode: Int, startTime: String?, endTime: String?, gid: Int, roomId: Int, roomName: String) {

        var sessionID = ""
        var bearerToken = ""
        activity?.let { activity ->
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
            bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
            Log.i("BookGSRFragment", "$bearerToken");

        }
        Log.i("BookGSRFragment", "Bearer $bearerToken")
        Log.i("BookGSRFragment", "Start $startTime")
        Log.i("BookGSRFragment", "End $endTime")
        Log.i("BookGSRFragment", "GID $gid")
        Log.i("BookGSRFragment", "ID $roomId")
        Log.i("BookGSRFragment", "Room Name $roomName")
        mStudentLife.bookGSR(
                //Passing the values
                bearerToken,
                startTime,
                endTime,
                gid,
                roomId,
                roomName,

                //Creating an anonymous callback
                object : Callback<GSRBookingResult> {
                    override fun success(result: GSRBookingResult, response: Response) {
                        //Displaying the output as a toast and go back to GSR fragment
                        if (result.getDetail().equals("success")) {
                            Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()
                            val textTitle = "GSR Booking"
                            val textContent = "Your GSR will be ready in 10 minutes"
                            val notificationId = id
                            Log.d("TAGGO", "success: pre builder" )
                            var builder = context?.let {
                                NotificationCompat.Builder(it, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.pennmobile_logo)
                                    .setContentTitle(textTitle)
                                    .setContentText(textContent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            }
                            Log.d("TAGGO", "success: Post builder" )
                            with(NotificationManagerCompat.from(requireContext())) {
                                // notificationId is a unique int for each notification that you must define
                                if (builder != null) {
                                    notify(notificationId, builder.build())
                                }
                            }
                            Log.d("TAGGO", "success: notif made" ) //set up notif here
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
                        Log.e("BookGSRFragment", "Error booking gsr", error)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {

        fun newInstance(gsrID: String, gsrLocationCode: String, startTime: String, endTime: String, gid: Int, roomId: Int, roomName: String): BookGsrFragment {
            val fragment = BookGsrFragment()
            val args = Bundle()
            args.putString("gsrID", gsrID)
            args.putString("gsrLocationCode", gsrLocationCode)
            args.putString("startTime", startTime)
            args.putString("endTime", endTime)
            args.putInt("gid", gid)
            args.putInt("id", roomId)
            args.putString("roomName", roomName)
            fragment.arguments = args
            return fragment
        }
    }

}