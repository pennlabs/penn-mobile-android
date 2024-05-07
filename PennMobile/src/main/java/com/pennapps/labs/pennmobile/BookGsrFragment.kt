package com.pennapps.labs.pennmobile

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import com.pennapps.labs.pennmobile.databinding.GsrDetailsBookBinding
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class BookGsrFragment : Fragment() {
    private var _binding: GsrDetailsBookBinding? = null
    val binding get() = _binding!!

    private lateinit var mActivity: MainActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { arguments ->
            gsrID = arguments.getString("gsrID") ?: ""
            gsrLocationCode = arguments.getString("gsrLocationCode") ?: ""
            startTime = arguments.getString("startTime") ?: ""
            endTime = arguments.getString("endTime") ?: ""
            gid = arguments.getInt("gid")
            roomId = arguments.getInt("id")
            roomName = arguments.getString("roomName") ?: ""
        }
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.setTitle(R.string.gsr)
    }

    override fun onResume() {
        super.onResume()
        val mActivity: MainActivity = activity as MainActivity
        mActivity.setTitle(R.string.gsr)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GsrDetailsBookBinding.inflate(inflater, container, false)
        val view = binding.root

        firstNameEt = binding.firstName
        lastNameEt = binding.lastName
        emailEt = binding.gsrEmail
        submit = binding.submitGsr

        // get user email and name from shared preferences if it's already saved
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val email = sp.getString(getString(R.string.email_address), "")
        val firstName = sp.getString(getString(R.string.first_name), "")
        val lastName = sp.getString(getString(R.string.last_name), "")

        firstNameEt.setText(firstName)
        lastNameEt.setText(lastName)
        emailEt.setText(email)

        submit.setOnClickListener {
            if (firstNameEt.text.toString().matches("".toRegex()) || lastNameEt.text.toString().matches("".toRegex()) ||
                emailEt.text.toString().matches("".toRegex())
            ) {
                Toast.makeText(
                    activity,
                    "Please fill in all fields before booking",
                    Toast.LENGTH_LONG,
                ).show()
            } else if (!emailEt.text.toString().matches("""\w+@(seas\.|sas\.|wharton\.|nursing\.)?upenn\.edu""".toRegex())) {
                Toast.makeText(activity, "Please enter a valid Penn email", Toast.LENGTH_LONG).show()
            } else {
                submit.isClickable = false
                submit.background.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
                binding.loading.loadingPanel.visibility = View.VISIBLE
                bookGSR(Integer.parseInt(gsrID), gsrLocationCode, startTime, endTime, gid, roomId, roomName)
            }
        }

        mActivity.hideBottomBar()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bookGSR(
        gsrId: Int,
        gsrLocationCode: String,
        startTime: String?,
        endTime: String?,
        gid: Int,
        roomId: Int,
        roomName: String,
    ) {
        mActivity.mNetworkManager.getAccessToken {
            var bearerToken = ""
            activity?.let { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
                Log.i("BookGSRFragment", bearerToken)
            }
            Log.i("BookGSRFragment", "Bearer $bearerToken")
            Log.i("BookGSRFragment", "Start $startTime")
            Log.i("BookGSRFragment", "End $endTime")
            Log.i("BookGSRFragment", "GID $gid")
            Log.i("BookGSRFragment", "ID $roomId")
            Log.i("BookGSRFragment", "Room Name $roomName")

            mStudentLife.bookGSR(
                // Passing the values
                bearerToken,
                startTime,
                endTime,
                gid,
                roomId,
                roomName,
                // Creating an anonymous callback
                object : Callback<GSRBookingResult> {
                    override fun success(
                        result: GSRBookingResult,
                        response: Response,
                    ) {
                        // Displaying the output as a toast and go back to GSR fragment
                        if (result.getDetail().equals("success")) {
                            Toast.makeText(activity, "GSR successfully booked", Toast.LENGTH_LONG).show()

                            // Save user info in shared preferences
                            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                            val editor = sp.edit()
                            editor.putString(getString(R.string.first_name), firstNameEt.text.toString())
                            editor.putString(getString(R.string.last_name), lastNameEt.text.toString())
                            editor.putString(getString(R.string.email_address), emailEt.text.toString())
                            editor.apply()
                        } else {
                            Toast.makeText(activity, "GSR booking failed", Toast.LENGTH_LONG).show()
                            Log.e("BookGsrFragment", "GSR booking failed with " + result.getError())
                        }
                        // go back to GSR fragment
                        binding.loading.loadingPanel.visibility = View.GONE
                        activity?.onBackPressed()
                    }

                    override fun failure(error: RetrofitError) {
                        // If any error occurred displaying the error as toast
                        Log.e("BookGSRFragment", "Error booking gsr", error)
                        Toast.makeText(activity, "An error has occurred. Please try again.", Toast.LENGTH_LONG).show()
                        binding.loading.loadingPanel.visibility = View.GONE
                        activity?.onBackPressed()
                    }
                },
            )
        }
    }

    companion object {
        fun newInstance(
            gsrID: String,
            gsrLocationCode: String,
            startTime: String,
            endTime: String,
            gid: Int,
            roomId: Int,
            roomName: String,
        ): BookGsrFragment {
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
