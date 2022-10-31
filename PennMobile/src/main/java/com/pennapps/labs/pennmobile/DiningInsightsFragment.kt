package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.adapters.DiningInsightsCardAdapter
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.CampusExpressNetworkManager
import com.pennapps.labs.pennmobile.classes.DiningBalances
import com.pennapps.labs.pennmobile.classes.DiningBalancesList
import com.pennapps.labs.pennmobile.classes.DiningInsightCell
import com.pennapps.labs.pennmobile.classes.DollarsSpentCell
import kotlinx.android.synthetic.main.fragment_dining.*
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_dining_insights.*
import kotlinx.android.synthetic.main.fragment_dining_insights.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


/**
 * Dining Insights Fragment
 * Created by Julius Snipes
 */
class DiningInsightsFragment : Fragment() {

    private lateinit var mActivity : MainActivity
    private lateinit var mCampusExpress: CampusExpress
    private lateinit var networkManager: CampusExpressNetworkManager
    private lateinit var cells : ArrayList<DiningInsightCell>
    private lateinit var insightsrv : RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCampusExpress = MainActivity.campusExpressInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dining_insights, container, false)
        view.dining_insights_refresh?.setOnRefreshListener { refresh() }
        view.dining_insights_refresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.insightsrv.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false)
        networkManager = CampusExpressNetworkManager(mActivity)
        val diningBalance = DollarsSpentCell()
        diningBalance.type = "dining_balance"
        val diningDollarsPredictionsCell = DiningInsightCell()
        diningDollarsPredictionsCell.type = "dining_dollars_predictions"
        val diningSwipesPredictionsCell = DiningInsightCell()
        diningSwipesPredictionsCell.type = "dining_swipes_predictions"
        cells = ArrayList()
        cells.add(diningBalance)
        cells.add(diningDollarsPredictionsCell)
        cells.add(diningSwipesPredictionsCell)
        insightsrv = view.insightsrv
        insightsrv.adapter = DiningInsightsCardAdapter(cells)
        val networkManager = CampusExpressNetworkManager(mActivity)
        val accessToken = networkManager.getAccessToken()
        if (accessToken == "") {
            val fragment = CampusExpressLoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.dining_insights_page, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("DiningInsightsFragment")
                .commit()
        }
        getInsights(accessToken)
        // Inflate the layout for this fragment
        return view
    }



    override fun onResume() {
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refresh() {
        val accessToken = networkManager.getAccessToken()
        if (accessToken == "") {
            dining_insights_refresh?.isRefreshing = false
            val fragment = CampusExpressLoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.dining_insights_page, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("DiningInsightsFragment")
                .commit()
        } else {
            dining_insights_refresh?.isRefreshing = true
            getInsights(accessToken)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInsights(accessToken: String?) {
        val bearerToken = "Bearer $accessToken"
        mCampusExpress.getCurrentDiningBalances(bearerToken).subscribe( { t: DiningBalances? ->
            activity?.runOnUiThread {
                val diningBalanceCell = cells[0]
                diningBalanceCell.diningBalances = t
                (insightsrv.adapter as DiningInsightsCardAdapter).notifyItemChanged(0)
                dining_insights_refresh?.isRefreshing = false
            } },
            { throwable ->
            activity?.runOnUiThread {
                Log.e("DiningInsightsFragment", "Error getting balances", throwable)
                dining_insights_refresh?.isRefreshing = false
            }
        })
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedCurrentDate = current.format(formatter)
        mCampusExpress.getPastDiningBalances(bearerToken, DiningInsightsCardAdapter.START_DAY_OF_SEMESTER, formattedCurrentDate).subscribe( { t: DiningBalancesList? ->
            activity?.runOnUiThread {
                cells[1].diningBalancesList = t
                cells[2].diningBalancesList = t
                (insightsrv.adapter as DiningInsightsCardAdapter).notifyItemChanged(1)
                (insightsrv.adapter as DiningInsightsCardAdapter).notifyItemChanged(2)
                dining_insights_refresh?.isRefreshing = false
            } },
            { throwable ->
                activity?.runOnUiThread {
                    Log.e("DiningInsightsFragment", "Error getting balances", throwable)
                    dining_insights_refresh?.isRefreshing = false
                }
            })

    }
    
}