package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeSavedBinding

class SublesseeSavedFragment() : Fragment() {

    //create binding
    private var _binding : FragmentSublesseeSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var sublesseeSavedPagerAdapter: SublesseeSavedPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var toMarketplaceButton: Button

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
        _binding = FragmentSublesseeSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sublesseeSavedPagerAdapter = SublesseeSavedPagerAdapter(this)
        viewPager = binding.sublesseeSavedViewPager
        viewPager.adapter = sublesseeSavedPagerAdapter
        val tabLayout = binding.sublesseeSavedTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Saved"
            }
            else {
                tab.text = "Applied"
            }
        }.attach()

        toMarketplaceButton = binding.sublesseeSavedToMarketplaceButton

        toMarketplaceButton.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SubletteeMarketplace())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}