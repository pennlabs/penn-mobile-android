package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.adapters.SubletterListingPagerAdapter

class SubletterListingHolderFragment : Fragment() {
    lateinit var subletterListingPagerAdapter: SubletterListingPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subletter_listing, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subletterListingPagerAdapter = SubletterListingPagerAdapter(this)
        viewPager = view.findViewById(R.id.viewPager2)
        viewPager.adapter = subletterListingPagerAdapter


    }


}