package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pennapps.labs.pennmobile.api.Labs
import kotlinx.android.synthetic.main.gsr_details_book.view.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class BookGsrFragment : Fragment() {
    
    internal var firstName: EditText? = null
    internal var lastName: EditText? = null
    internal var email: EditText? = null
    internal var submit: Button? = null
    private var unbinder: Unbinder? = null

    private var mLabs: Labs? = null
    private var gsrID: String? = null
    private var gsrLocationCode: String? = null
    private var startTime: String? = null
    private var endTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            gsrID = arguments!!.getString("gsrID")
            gsrLocationCode = arguments!!.getString("gsrLocationCode")
            startTime = arguments!!.getString("startTime")
            endTime = arguments!!.getString("endTime")
        }
        mLabs = MainActivity.getLabsInstance()
        activity!!.setTitle(R.string.gsr)
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.gsr)
        (activity as MainActivity).setNav(R.id.nav_gsr)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.gsr_details_book, container, false)

        firstName = v.first_name
        lastName = v.last_name
        email = v.gsr_email
        submit = v.submit_gsr


        unbinder = ButterKnife.bind(this, v)
        submit?.let { submit ->
            submit.setOnClickListener {
                if (firstName!!.text.toString().matches("".toRegex()) || lastName!!.text.toString().matches("".toRegex())
                        || email!!.text.toString().matches("".toRegex())) {
                    Toast.makeText(activity, "Please fill in all fields before booking",
                            Toast.LENGTH_LONG).show()
                } else {
                    bookGSR(Integer.parseInt(gsrID), Integer.parseInt(gsrLocationCode), startTime, endTime)
                }
            }
            return v
        }
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    private fun bookGSR(gsrId: Int, gsrLocationCode: Int, startTime: String?, endTime: String?) {


        mLabs!!.bookGSR(

                //Passing the values
                gsrLocationCode,
                gsrId,
                startTime,
                endTime,
                firstName!!.text.toString(),
                lastName!!.text.toString(),
                email!!.text.toString(),
                "Penn Mobile GSR",
                "2158986533",
                "2-3",

                //Creating an anonymous callback
                object : Callback<Response> {
                    override fun success(result: Response, response: Response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        var reader: BufferedReader? = null

                        //An string to store output from the server
                        val output = ""

                        try {
                            //Initializing buffered reader
                            reader = BufferedReader(InputStreamReader(result.body.`in`()))

                            //uncomment for debugging
                            /*
                            String s = null;

                            while ((s=reader.readLine())!=null)
                            {

                                Log.e("res", s);
                            }
                            */
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

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