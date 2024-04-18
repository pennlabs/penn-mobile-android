package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.databinding.FragmentSubletDetailsBinding

class SubletDetailsFragment(private val dataModel: SublettingViewModel, private val subletNumber: Int) : Fragment() {
    private var _binding: FragmentSubletDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife : StudentLife
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mStudentLife = MainActivity.studentLifeInstance


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletDetailsBinding.inflate(inflater, container, false)

        val sublet : Sublet = dataModel.getSublet(subletNumber)
        binding.titleText.text = sublet.title
        binding.priceText.text = "$" + sublet.price.toString()
        binding.addressText.text = sublet.address
        binding.datesText.text = sublet.startDate + " to " + sublet.endDate
        binding.descriptionText.text = sublet.description ?: "None"
        binding.amenitiesText.text = sublet.amenities?.joinToString(", ") ?: "No amenities available"
        context?.let {
            Glide.with(it)
                .load(sublet.images?.get(0)?.imageUrl)
                .centerCrop() // optional - adjust as needed
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.subletImage)
        }

        binding.availableButton.setOnClickListener{
            dataModel.deleteSublet(mActivity, subletNumber)
        }




        return binding.root
    }
}