package com.pennapps.labs.pennmobile


import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import kotlinx.android.synthetic.main.fragment_huntsman_gsrlogin.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


class HuntsmanGSRLogin : Fragment() {

    // gsr details
    private lateinit var gsrID: String
    private lateinit var gsrLocationCode: String
    private lateinit var startTime: String
    private lateinit var endTime: String

    private lateinit var mLabs: Labs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        arguments?.let {arguments ->
            gsrID = arguments.getString("gsrID") ?: ""
            gsrLocationCode = arguments.getString("gsrLocationCode") ?: ""
            startTime = arguments.getString("startTime") ?: ""
            endTime = arguments.getString("endTime") ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_huntsman_gsrlogin, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val sessionid = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
        // load Huntsman website if no sessionid
        if (sessionid == "") {
            loadWebpage()
        } else {
            bookHuntsmanGSR(sessionid)
        }
    }

    private fun loadWebpage() {
        // Get the web view settings instance
        webViewGSR.webViewClient = object : WebViewClient() {

            // Called every time a URL finishes loading, not just when the first URL finishes loading
            override fun onPageFinished(view : WebView, url : String) {
                // extract sessionid after user logs in
                if (url == "https://apps.wharton.upenn.edu/gsr/") {
                    var sessionid = ""
                    val cookies = CookieManager.getInstance().getCookie(url).split(";")
                    for (cookie in cookies){
                        if (cookie.take(11) == " sessionid=") {
                            sessionid = cookie.substring(11)
                            break
                        }
                    }

                    // save sessionid in shared preferences
                    activity?.let { activity ->
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        val editor = sp.edit()
                        editor.putString(getString(R.string.huntsmanGSR_SessionID), sessionid)
                        editor.apply()
                    }
                    if (startTime.substring(9,13) == "2330") {
                        val newDay = endTime[7] + 1
                        var newEndTime = endTime.substring(0,7) + newDay + endTime.substring(8,endTime.length)
                        endTime = newEndTime
                    }
                    bookHuntsmanGSR(sessionid)
                }
            }
        }
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    // performs POST request and redirects user to GSR booking fragment
    private fun bookHuntsmanGSR(sessionid : String) {
        mLabs.let { mLabs ->
            mLabs.bookGSR(
                    //Passing the values
                    sessionid,
                    Integer.parseInt(gsrLocationCode),
                    Integer.parseInt(gsrID),
                    startTime,
                    endTime,
                    "firstname",
                    "lastname",
                    "email",
                    "Penn Mobile GSR",
                    "2158986533",
                    "2-3",

                    //Creating an anonymous callback
                    object : Callback<GSRBookingResult> {
                        override fun success(result: GSRBookingResult, response: Response) {
                            //Display the output as a toast
                            if (result.getResults() == true) {
                                Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(activity, "GSR booking failed: " + result.getError(), Toast.LENGTH_LONG).show()
                                activity?.let { activity ->
                                    val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                                    val editor = sp.edit()
                                    editor.remove(getString(R.string.huntsmanGSR_SessionID))
                                    editor.apply()
                                }
                            }
                            // redirect user
                            val huntsmanGSRLogin = GsrFragment()
                            val fragmentManager = (context as MainActivity).supportFragmentManager
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, huntsmanGSRLogin)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                        }

                        override fun failure(error: RetrofitError) {
                            //If any error occurred display the error as toast
                            val result = error.getBodyAs(GSRBookingResult::class.java) as GSRBookingResult
                            Toast.makeText(activity, result.getError() ?: "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                            // redirect user
                            val huntsmanGSRLogin = GsrTabbedFragment()
                            val fragmentManager = (context as MainActivity).supportFragmentManager
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, huntsmanGSRLogin)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                        }
                    }
            )
        }
    }

    companion object {

        fun newInstance(gsrID: String, gsrLocationCode: String, startTime: String, endTime: String): HuntsmanGSRLogin {
            val fragment = HuntsmanGSRLogin()
            val args = Bundle()
            args.putString("gsrID", gsrID)
            args.putString("gsrLocationCode", gsrLocationCode)
            args.putString("startTime", startTime)
            args.putString("endTime", endTime)
            fragment.arguments = args
            return fragment
        }
    }
}
