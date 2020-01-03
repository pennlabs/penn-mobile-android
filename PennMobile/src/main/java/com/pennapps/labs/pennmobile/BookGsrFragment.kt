package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.Labs
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
    internal lateinit var submit: Button

    private lateinit var mLabs: Labs

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
        mLabs = MainActivity.getLabsInstance()
        activity?.let {activity ->
            activity.setTitle(R.string.gsr)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let { activity ->
            activity.setTitle(R.string.gsr)
        }
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

        var sessionID = ""
        activity?.let { activity ->
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
        }

        mLabs?.let { mLabs ->
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
                                Toast.makeText(activity, "GSR booking failed with " + result.getError(), Toast.LENGTH_LONG).show()
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

}// Required empty public constructor