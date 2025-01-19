package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife

// TODO: Rename parameter arguments, choose names that match


/**
 * A simple [Fragment] subclass.
 * Use the [SubletterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubletterFragment : Fragment() {
    lateinit var subletterPagerAdapter: SubletterPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2
    private lateinit var mStudentLife : StudentLife
    private lateinit var dataModel : SublettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        mStudentLife = MainActivity.studentLifeInstance
        dataModel = SublettingViewModel(mActivity, mStudentLife)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_subletter_view, container, false)
        viewPager = rootView.findViewById(R.id.subletter_view_pager)
        subletterPagerAdapter = SubletterPagerAdapter(this, dataModel)
        viewPager.adapter = subletterPagerAdapter

        val tabLayout: TabLayout = rootView.findViewById(R.id.subletter_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0) {
                tab.text = "Posted"
            } else {
                tab.text = "Drafts"
            }

        }.attach()

        return rootView

    }





}