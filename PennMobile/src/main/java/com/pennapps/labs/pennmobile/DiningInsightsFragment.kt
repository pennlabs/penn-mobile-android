package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.adapters.DiningInsightsCardAdapter
import com.pennapps.labs.pennmobile.classes.DiningInsightCell
import com.pennapps.labs.pennmobile.classes.DollarsSpentCell
import kotlinx.android.synthetic.main.fragment_dining.*
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_dining.view.dining_swiperefresh
import kotlinx.android.synthetic.main.fragment_dining_insights.*
import kotlinx.android.synthetic.main.fragment_dining_insights.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [DiningInsightsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiningInsightsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dining_insights, container, false)
        view.dining_insights_refresh?.setOnRefreshListener { getInsights() }
        view.dining_insights_refresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.insightsrv.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false)
        val dollarsSpent = DollarsSpentCell()
        dollarsSpent.type = "dining_dollars_spent"
        val cells = ArrayList<DiningInsightCell>()
        cells.add(dollarsSpent)
        view.insightsrv.adapter = DiningInsightsCardAdapter(cells)
        // Inflate the layout for this fragment
        return view
    }

    private fun getInsights() {
        dining_insights_refresh?.isRefreshing = false
    }
    
}