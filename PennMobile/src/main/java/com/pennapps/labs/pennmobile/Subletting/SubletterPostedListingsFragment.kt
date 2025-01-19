package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding

class SubletterPostedListingsFragment() : Fragment() {
    private var _binding: FragmentSubletterPostedListingsBinding? = null
    private val binding get() = _binding!!

    //recyclerview adapters and layout manager
    lateinit var sublettingRecyclerView: RecyclerView
    lateinit var newLayoutManager: GridLayoutManager
    lateinit var sublettingList: ArrayList<Sublet>
    lateinit var myAdapter: PostedSubletsListAdapter
    lateinit var dataModel: SublettingViewModel

    //api manager
    private lateinit var mStudentLife: StudentLife
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        dataModel = SublettingViewModel(mActivity, mStudentLife)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletterPostedListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sublettingRecyclerView = binding.postedSubletsList
        newLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        sublettingRecyclerView.layoutManager = newLayoutManager

        dataModel.getPostedSublets(mActivity)
        myAdapter = PostedSubletsListAdapter(dataModel)
        dataModel.postedSubletsList.observe(viewLifecycleOwner) { sublets ->
            binding.listingsRefreshLayout.isRefreshing = false

            sublettingList = sublets
            myAdapter.notifyDataSetChanged()

            if (sublets.size > 0) {
                binding.postedHouseImage.visibility =  View.GONE
                binding.postedNoListingsText.visibility =  View.GONE
            } else {
                binding.postedHouseImage.visibility =  View.VISIBLE
                binding.postedNoListingsText.visibility =  View.VISIBLE
            }
        }

        sublettingRecyclerView.adapter = myAdapter

        binding.listingsRefreshLayout.setOnRefreshListener {
            dataModel.getPostedSublets(mActivity)
        }

        binding.postedAddListingButton.setOnClickListener{
            navigateCreateNewListing()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}