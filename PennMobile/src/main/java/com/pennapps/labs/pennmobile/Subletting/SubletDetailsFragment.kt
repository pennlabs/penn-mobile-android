package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentSubletDetailsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

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



        val startDate = LocalDate.parse(sublet.startDate, inputFormatter)
        val endDate = LocalDate.parse(sublet.endDate, inputFormatter)

// Format the dates to "Month Day, Year"
        val formattedStartDate = startDate.format(outputFormatter)
        val formattedEndDate = endDate.format(outputFormatter)

// Set the formatted dates to the TextView
        binding.datesText.text = "$formattedStartDate to $formattedEndDate"
        //binding.datesText.text = sublet.startDate + " to " + sublet.endDate
        binding.descriptionText.text = sublet.description ?: "None"
        binding.amenitiesText.text = sublet.amenities?.joinToString(", ") ?: "No amenities available"

        val images : List<SubletImage>? = sublet.images

        val imageViews = listOf(
            binding.image1,
            binding.image2,
            binding.image3,
            binding.image4,
            binding.image5,
            binding.image6
        )

        var count = 0
        if (images != null) {
            while (count < sublet.images.size && count < imageViews.size) {
                val imageUrl = sublet.images[count]?.imageUrl
                context?.let {
                    Glide.with(it)
                        .load(imageUrl)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageViews[count])  // Dynamically load into the right image view
                }
                count++
            }
        }

        binding.availableButton.setOnClickListener{
            dataModel.deleteSublet(mActivity, subletNumber)
        }

        binding.editText.setOnClickListener{
            navigateEditListing()
        }

        binding.deleteText.setOnClickListener{
            dataModel.deleteSublet(mActivity, subletNumber)

        }




        return binding.root
    }

    private fun navigateEditListing() {
        val mainActivity = context as MainActivity

        val fragment = SubletEditFragment(dataModel, subletNumber)

        val fragmentManager = mainActivity.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment, "NEW_LISTING_FRAGMENT")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}