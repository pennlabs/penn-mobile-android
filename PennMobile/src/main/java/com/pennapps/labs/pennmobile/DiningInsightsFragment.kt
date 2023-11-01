package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.pennapps.labs.pennmobile.databinding.FragmentDiningInsightsBinding

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    private var _binding : FragmentDiningInsightsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCampusExpress = MainActivity.campusExpressInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentDiningInsightsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.diningInsightsRefresh.setOnRefreshListener { refresh() }
        binding.diningInsightsRefresh.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        binding.insightsrv.layoutManager = LinearLayoutManager(
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
        insightsrv = binding.insightsrv
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isOnline(context)) {
            binding.internetConnectionDiningInsights.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageDiningInsights.text = "Not Connected to Internet"
            binding.internetConnectionDiningInsights.visibility = View.VISIBLE
            binding.diningInsightsRefresh.isRefreshing = false
            return
        } else {
            binding.internetConnectionDiningInsights.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun refresh() {
        val accessToken = networkManager.getAccessToken()
        if (accessToken == "") {
            binding.diningInsightsRefresh.isRefreshing = false
            val fragment = CampusExpressLoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.dining_insights_page, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("DiningInsightsFragment")
                .commit()
        } else {
            binding.diningInsightsRefresh.isRefreshing = true
            getInsights(accessToken)
        }
    }




    private fun getInsights(accessToken: String?) {
        if (!isOnline(context)) {
            binding.internetConnectionDiningInsights.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageDiningInsights.setText("Not Connected to Internet")
            binding.internetConnectionDiningInsights.visibility = View.VISIBLE
            binding.diningInsightsRefresh.isRefreshing = false
            return
        } else {
            binding.internetConnectionDiningInsights.visibility = View.GONE
        }
        val bearerToken = "Bearer $accessToken"
        mCampusExpress.getCurrentDiningBalances(bearerToken).subscribe( { t: DiningBalances? ->
            activity?.runOnUiThread {
                val diningBalanceCell = cells[0]
                diningBalanceCell.diningBalances = t
                (insightsrv.adapter as DiningInsightsCardAdapter).notifyItemChanged(0)
                binding.diningInsightsRefresh.isRefreshing = false
            } },
            { throwable ->
            activity?.runOnUiThread {
                Log.e("DiningInsightsFragment", "Error getting balances", throwable)
                binding.diningInsightsRefresh.isRefreshing = false
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
                binding.diningInsightsRefresh.isRefreshing = false
            } },
            { throwable ->
                activity?.runOnUiThread {
                    Log.e("DiningInsightsFragment", "Error getting balances", throwable)
                    binding.diningInsightsRefresh.isRefreshing = false
                }
            })

    }
    
}