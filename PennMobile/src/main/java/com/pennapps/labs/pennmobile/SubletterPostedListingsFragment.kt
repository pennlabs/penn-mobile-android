package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.adapters.PostedSubletsListAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingModel
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding
import kotlinx.android.synthetic.main.fragment_subletter_posted_listings.listings_refresh_layout

class SubletterPostedListingsFragment(private val dataModel: SublettingViewModel) : Fragment() {
    private var _binding: FragmentSubletterPostedListingsBinding? = null
    private val binding get() = _binding!!

    //recyclerview adapters and layout manager
    lateinit var sublettingRecyclerView: RecyclerView
    lateinit var newLayoutManager: GridLayoutManager
    lateinit var sublettingList: ArrayList<Sublet>
    lateinit var myAdapter: PostedSubletsListAdapter

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
        // Inflate the layout for this fragment
        _binding = FragmentSubletterPostedListingsBinding.inflate(inflater, container, false)
        binding.postedAddListingButton.setOnClickListener{
            navigateCreateNewListing()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sublettingRecyclerView = binding.postedSubletsList
        newLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        sublettingRecyclerView.layoutManager = newLayoutManager

        //delete  later, Trini's fake data testing
        sublettingRecyclerView.setHasFixedSize(true)
        sublettingList = setUpData()
        if (sublettingList.isNotEmpty()) {
            binding.postedNoListingsText.visibility = View.GONE;


        }

        myAdapter = PostedSubletsListAdapter(sublettingList)
        sublettingRecyclerView.adapter = myAdapter

    }


    private fun navigateCreateNewListing() {
        val mainActivity = context as MainActivity

        val fragment = NewListingsFragment(dataModel)

        val fragmentManager = mainActivity.supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "NEW_LISTING_FRAGMENT")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    private fun setUpData(): ArrayList<Sublet> {

        var sublettingList = ArrayList<Sublet>()

        val sublettingImages = intArrayOf(
                R.drawable.dining_gourmet_grocer,
                R.drawable.dining_hillel,
                R.drawable.dining_mcclelland,
                R.drawable.dining_kceh,
                R.drawable.dining_commons
        )

        val sublettingNames = arrayOf(
                "The Radian",
                "The Chestnut",
                "Axis",
                "The Radian",
                "The Speakeasy"
        )

        val sublettingPrices = intArrayOf(
                900,
                1000,
                1200,
                500,
                400
        )

        val sublettingBedrooms = arrayOf(
                2,
                3,
                1,
                4,
                10
        )

        val sublettingBathrooms = arrayOf(
                10,
                5,
                0,
                1,
                3
        )

        for (i in sublettingImages.indices)
            sublettingList.add(Sublet(title = sublettingNames[i],
                    minPrice = sublettingPrices[i], beds = sublettingBedrooms[i],
                    baths = sublettingBathrooms[i]))

        return sublettingList


    }

}