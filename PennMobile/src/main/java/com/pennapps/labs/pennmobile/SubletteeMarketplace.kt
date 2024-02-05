package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.adapters.SublettingListAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.SublettingModel
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeMarketplaceBinding
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 * Use the [SubletteeMarketplace.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubletteeMarketplace : Fragment() {


    private var _binding : FragmentSubletteeMarketplaceBinding? = null
    private val binding get() = _binding!!

    //spinner adapter

    private lateinit var sortByAdapter: ArrayAdapter<String>
    private lateinit var sortBySpinner: Spinner

    //recyclerview adapters and layout manager
    lateinit var sublettingRecyclerView: RecyclerView
    lateinit var newLayoutManager: GridLayoutManager
    lateinit var sublettingList: ArrayList<SublettingModel>
    lateinit var myAdapter: SublettingListAdapter

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
        container?.removeAllViews()
        _binding = FragmentSubletteeMarketplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sortByAdapter = ArrayAdapter(mActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arrayOf("Price", "Location"))
        sortBySpinner = binding.subletteeMarketplaceSort
        sortBySpinner.adapter = sortByAdapter

        sublettingRecyclerView = binding.subletteeMarketplaceList
        newLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        sublettingRecyclerView.layoutManager = newLayoutManager

        //delete for later, testing
        sublettingRecyclerView.setHasFixedSize(true)
        sublettingList = setUpData()

        myAdapter = SublettingListAdapter(sublettingList)
        sublettingRecyclerView.adapter = myAdapter
    }

    //function to put in fake data- will get rid of once I get backend data in
    private fun setUpData(): ArrayList<SublettingModel> {

        var sublettingList = ArrayList<SublettingModel>()

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

        val sublettingNegotiablePrices = arrayOf(
                true,
                false,
                true,
                true,
                false
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
            sublettingList.add(SublettingModel(sublettingImages[i], sublettingNames[i],
                    sublettingPrices[i], sublettingNegotiablePrices[i], sublettingBedrooms[i],
                    sublettingBathrooms[i], 2, 2))

        return sublettingList


    }
}