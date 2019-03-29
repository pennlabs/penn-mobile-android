package com.pennapps.labs.pennmobile


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
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
            gsrID = arguments.getString("gsrID")
            gsrLocationCode = arguments.getString("gsrLocationCode")
            startTime = arguments.getString("startTime")
            endTime = arguments.getString("endTime")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_huntsman_gsrlogin, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("@@@@@", "opened huntsman gsr login")
        loadWebpage()
    }

    private fun loadWebpage() {
        // Get the web view settings instance

        Log.d("@@@@@@@", "loadwebpage called")
        webViewGSR.webViewClient = object : WebViewClient() {

            // Called every time a URL finishes loading, not just when the first URL finishes loading
            override fun onPageFinished(view : WebView, url : String) {
                Log.d("@@@@@@", "page finished loading")
                if (url == "https://apps.wharton.upenn.edu/gsr/") {
                    // val cookies = CookieManager.getInstance().getCookie(url)
                    var sessionid = ""
                    val cookies = CookieManager.getInstance().getCookie(url).split(";")
                    for (cookie in cookies){
                        if (cookie.take(11) == " sessionid=") {
                            sessionid = cookie.substring(11)
                            Log.d("@@@@", "Session ID: " + sessionid)
                            break
                        }
                    }

                    // set up shared preferences
                    activity?.let { activity ->
                        Log.d("@@@@ activity", "storing sessionid " + sessionid)
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        val editor = sp.edit()
                        editor.putString(getString(R.string.huntsmanGSR_SessionID), sessionid)
                        editor.apply()
                    }
                    Log.d("@@@@@", "booking GSR now, $startTime - $endTime")

                    mLabs?.let { mLabs ->
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
                                        //Displaying the output as a toast
                                        Log.d("@@@@ string", result.toString())
                                        Log.d("@@@@@ results", result.getResults()?.toString() ?: "results is null")
                                        Log.d("@@@@@ error", result.getError() ?: "error is null")
                                        if (result.getResults() == true) {
                                            Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()
                                        }
                                        else {
                                            Toast.makeText(activity, "GSR booking failed with " + result.getError(), Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun failure(error: RetrofitError) {
                                        //If any error occurred displaying the error as toast
                                        Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                                    }
                                }
                        )
                    }
                }
            }
        }
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
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
