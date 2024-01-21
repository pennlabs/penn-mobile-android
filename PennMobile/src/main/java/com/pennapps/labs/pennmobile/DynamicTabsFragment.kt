package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.adapters.DynamicTabsAdapter
import com.pennapps.labs.pennmobile.classes.Tab
import kotlinx.android.synthetic.main.fragment_dynamic_tabs.view.*


class DynamicTabsFragment : Fragment() {

    lateinit var dynamicTabsAdapter: DynamicTabsAdapter
    lateinit var tabs : List<Tab>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dynamic_tabs, container, false)
        tabs = listOf(Tab("Dining"), Tab("GSR"), Tab("Laundry"))
//        val itemTouchHelper = object : ItemTouchHelper() {
//        }
        dynamicTabsAdapter = DynamicTabsAdapter(tabs)
        view.rvDynamicTabs.adapter = dynamicTabsAdapter
        view.rvDynamicTabs.layoutManager = LinearLayoutManager(context)
        return view
    }

}