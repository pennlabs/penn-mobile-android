package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import butterknife.ButterKnife
import com.pennapps.labs.pennmobile.api.Labs
import kotlinx.android.synthetic.main.gsr_details_book.view.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class BookGsrFragment : Fragment() {

    // fields for booking
    internal lateinit var firstName: EditText
    internal lateinit var lastName: EditText
    internal lateinit var email: EditText
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
            gsrID = arguments.getString("gsrID")
            gsrLocationCode = arguments.getString("gsrLocationCode")
            startTime = arguments.getString("startTime")
            endTime = arguments.getString("endTime")
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
        (activity as MainActivity).setNav(R.id.nav_gsr)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.gsr_details_book, container, false)

        firstName = v.first_name
        lastName = v.last_name
        email = v.gsr_email
        submit = v.submit_gsr


        submit?.let { submit ->
            submit.setOnClickListener {
                firstName?.let {firstName ->
                    lastName?.let { lastName ->
                        email?.let {email ->
                            if (firstName.text.toString().matches("".toRegex()) || lastName.text.toString().matches("".toRegex())
                                    || email.text.toString().matches("".toRegex())) {
                                Toast.makeText(activity, "Please fill in all fields before booking",
                                        Toast.LENGTH_LONG).show()
                            } else {
                                bookGSR(Integer.parseInt(gsrID), Integer.parseInt(gsrLocationCode), startTime, endTime)
                            }
                        }
                    }
                }
            }
            return v
        }
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun bookGSR(gsrId: Int, gsrLocationCode: Int, startTime: String?, endTime: String?) {

        mLabs?.let { mLabs ->
            mLabs.bookGSR(
                    //Passing the values
                    gsrLocationCode,
                    gsrId,
                    startTime,
                    endTime,
                    firstName?.text.toString(),
                    lastName?.text.toString(),
                    email?.text.toString(),
                    "Penn Mobile GSR",
                    "2158986533",
                    "2-3",

                    //Creating an anonymous callback
                    object : Callback<Response> {
                        override fun success(result: Response, response: Response) {
                            //Displaying the output as a toast
                            Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()
                        }

                        override fun failure(error: RetrofitError) {
                            //If any error occurred displaying the error as toast
                            Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
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