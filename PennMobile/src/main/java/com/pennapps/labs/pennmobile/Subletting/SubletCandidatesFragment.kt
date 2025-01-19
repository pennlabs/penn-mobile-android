package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentSubletCandidatesBinding

class SubletCandidatesFragment(private val subletNumber: Int) : Fragment() {
    private var _binding: FragmentSubletCandidatesBinding? = null
    private val binding get() = _binding!!

    lateinit var offerRecyclerView: RecyclerView
    lateinit var offerList: ArrayList<Offer>
    lateinit var offerAdapter: OfferListAdapter
    lateinit var offerViewModel: OfferViewModel

    private lateinit var mStudentLife: StudentLife

    private lateinit var mActivity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        offerViewModel = OfferViewModel(mActivity, mStudentLife)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletCandidatesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        offerRecyclerView = binding.offerList



        offerViewModel.getOffers(mActivity, subletNumber)
        offerAdapter = OfferListAdapter(offerViewModel)
        offerViewModel.offersList.observe(viewLifecycleOwner, { offers ->
            offerList = offers
            offerAdapter.notifyDataSetChanged()
        })
        offerRecyclerView.adapter = offerAdapter


    }

}