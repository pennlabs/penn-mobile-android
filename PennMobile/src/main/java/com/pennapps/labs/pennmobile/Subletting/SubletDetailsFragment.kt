package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.databinding.FragmentSubletDetailsBinding

class SubletDetailsFragment(private val dataModel: SublettingViewModel, private val subletNumber: Int) : Fragment() {
    private var _binding: FragmentSubletDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletDetailsBinding.inflate(inflater, container, false)

        val sublet : Sublet = dataModel.getSublet(subletNumber)
        binding.titleText.text = sublet.title
        binding.priceText.text = sublet.price.toString()
        binding.addressText.text = sublet.address
        binding.datesText.text = sublet.startDate + " to " + sublet.endDate
        binding.descriptionText.text = sublet.description




        return binding.root
    }
}