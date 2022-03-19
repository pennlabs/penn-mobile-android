package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.adapters.DiningPagerAdapter
import kotlinx.android.synthetic.main.fragment_dining_holder.*

class DiningHolderFragment : Fragment() {

    lateinit var pagerAdapter: DiningPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dining_holder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentManager = childFragmentManager;
        pagerAdapter = DiningPagerAdapter(fragmentManager, lifecycle)
        pager?.setAdapter(pagerAdapter)
    }
}