package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeDetailsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeMarketplaceBinding
import kotlinx.android.synthetic.main.fragment_sublessee_details.interested_sublet_button

class SublesseeDetailsFragment : Fragment() {

    private var _binding : FragmentSublesseeDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mActivity: MainActivity

    private lateinit var saveButton: Button
    private lateinit var interestedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSublesseeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveButton = binding.saveSubletButton
        interestedButton = binding.interestedSubletButton

        saveButton.setOnClickListener {
            saveButton.text = "Saved"
        }

        interestedButton.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SublesseeInterestForm())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }
}