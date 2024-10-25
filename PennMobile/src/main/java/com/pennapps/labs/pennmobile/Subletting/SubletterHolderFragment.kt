package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.adapters.SubletterPagerAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_dining_holder.view.appbar_home_holder

class SubletterHolderFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_subletting, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subletterPagerAdapter = SubletterPagerAdapter(this, dataModel)
        viewPager = view.findViewById(R.id.viewPager2)
        viewPager.adapter = subletterPagerAdapter


    }


    private fun initAppBar(view: View) {
        (view.appbar_home_holder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
    }
}