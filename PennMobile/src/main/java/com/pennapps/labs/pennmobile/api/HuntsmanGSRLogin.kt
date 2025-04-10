package com.pennapps.labs.pennmobile.api

import StudentLife
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentHuntsmanGsrloginBinding
import com.pennapps.labs.pennmobile.gsr.fragments.GsrTabbedFragment
import kotlinx.coroutines.launch

class HuntsmanGSRLogin : Fragment() {
    // gsr details
    private lateinit var gsrID: String
    private lateinit var gsrLocationCode: String
    private lateinit var startTime: String
    private lateinit var endTime: String
    private lateinit var roomName: String
    private var gid: Int = 0

    private lateinit var mStudentLife: StudentLife
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentHuntsmanGsrloginBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        arguments?.let { arguments ->
            gsrID = arguments.getString("gsrID") ?: ""
            gsrLocationCode = arguments.getString("gsrLocationCode") ?: ""
            startTime = arguments.getString("startTime") ?: ""
            endTime = arguments.getString("endTime") ?: ""
            gid = arguments.getInt("gid")
            roomName = arguments.getString("roomName") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHuntsmanGsrloginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val sessionid = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") ?: ""
        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "")
        // load Huntsman website if no sessionid
        if (sessionid == "") {
            loadWebpage()
        } else {
            bookHuntsmanGSR(bearerToken, sessionid)
        }
    }

    private fun loadWebpage() {
        // Get the web view settings instance
        binding.webViewGSR.webViewClient =
            object : WebViewClient() {
                // Called every time a URL finishes loading, not just when the first URL finishes loading
                override fun onPageFinished(
                    view: WebView,
                    url: String,
                ) {
                    // extract sessionid after user logs in
                    if (url == "https://apps.wharton.upenn.edu/gsr/") {
                        var sessionid = ""
                        val cookies = CookieManager.getInstance().getCookie(url).split(";")
                        for (cookie in cookies) {
                            if (cookie.take(11) == " sessionid=") {
                                sessionid = cookie.substring(11)
                                break
                            }
                        }

                        // save sessionid in shared preferences
                        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
                        val editor = sp.edit()
                        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "")
                        editor.putString(getString(R.string.huntsmanGSR_SessionID), sessionid)
                        editor.apply()
                        if (startTime.substring(9, 13) == "2330") {
                            val newDay = endTime[7] + 1
                            val newEndTime = endTime.substring(0, 7) + newDay + endTime.substring(8, endTime.length)
                            endTime = newEndTime
                        }
                        bookHuntsmanGSR(bearerToken, sessionid)
                    }
                }
            }
        binding.webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    // performs POST request and redirects user to GSR booking fragment
    private fun bookHuntsmanGSR(
        bearerToken: String,
        sessionID: String,
    ) {
        (activity as MainActivity).mNetworkManager.getAccessToken {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response =
                        mStudentLife.bookGSR(
                            // Passing the values
                            bearerToken,
                            startTime,
                            endTime,
                            gid,
                            Integer.parseInt(gsrID),
                            roomName,
                        )
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.getResults() == true) {
                            Toast
                                .makeText(mActivity, "GSR successfully booked", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Log.e("HuntsmanGSRLogin", "GSR booking failed: " + result?.getError())
                            Toast
                                .makeText(mActivity, "GSR booking failed", Toast.LENGTH_LONG)
                                .show()
                            val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
                            val editor = sp.edit()
                            editor.remove(getString(R.string.huntsmanGSR_SessionID))
                            editor.apply()
                        }
                        // redirect user
                        val gsrFragment = GsrTabbedFragment()
                        val fragmentManager = mActivity.supportFragmentManager
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, gsrFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    } else {
                        val error = Exception(response.errorBody()?.string() ?: "Unknown error")
                        Log.e("HuntsmanGSRLogin", "GSR booking failed $error")
                        Toast.makeText(mActivity, "GSR booking failed", Toast.LENGTH_LONG).show()
                        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
                        val editor = sp.edit()
                        editor.remove(getString(R.string.huntsmanGSR_SessionID))
                        editor.apply()
                        // redirect user
                        val gsrFragment = GsrTabbedFragment()
                        val fragmentManager = mActivity.supportFragmentManager
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.content_frame, gsrFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
