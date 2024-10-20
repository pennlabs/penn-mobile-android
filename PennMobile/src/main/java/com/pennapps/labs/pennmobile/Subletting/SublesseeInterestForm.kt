package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Offer
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeInterestFormBinding

class SublesseeInterestForm (id: Int): Fragment() {

    //create binding
    private var _binding : FragmentSublesseeInterestFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var interestButton: Button

    //api manager
    private lateinit var mStudentLife: StudentLife
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

        val bundle = Bundle()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSublesseeInterestFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interestButton = binding.sublesseeInterestSendButton

        //Need data validation
        interestButton.setOnClickListener {
            val offer = Offer(
                sublet = id,
                phoneNumber = "test",
                createdDate = "",
                message = "",
                user = "",
                email = ""
            )

            Toast.makeText(mActivity, "Your message has been sent!", Toast.LENGTH_LONG).show()
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SublesseeSavedFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}