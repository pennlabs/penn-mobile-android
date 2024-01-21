package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterDraftListingsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding

class SubletterDraftListingsFragment : Fragment() {
    private var _binding: FragmentSubletterDraftListingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletterDraftListingsBinding.inflate(inflater, container, false)
        binding.draftAddListingButton.setOnClickListener{
            navigateCreateNewListing()
        }
        return binding.root
    }

    private fun navigateCreateNewListing() {
        val mainActivity = activity as MainActivity
        val fragment = NewListingsFragment()

        val fragmentManager = mainActivity.supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "NEW_LISTING_FRAGMENT")
                .addToBackStack(null)
                .commitAllowingStateLoss()

    }
}