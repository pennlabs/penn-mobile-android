package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.classes.DiningInsightCell

class DiningInsightsCardAdapter(private var cells: ArrayList<DiningInsightCell>) :
        RecyclerView.Adapter<DiningInsightsCardAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    companion object {
        // Types of Home Cells
        private const val NOT_SUPPORTED = -1
        private const val DINING_BALANCE = 0
        private const val DINING_DOLLARS_SPENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity

        return when (viewType) {
            DINING_BALANCE -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.dining_balances_card, parent, false))
            }
            DINING_DOLLARS_SPENT -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.dining_spent_card, parent, false))
            }
            NOT_SUPPORTED -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cell = cells[position]
        when (cell.type) {
            "dining_balance" -> bindDiningBalanceCells(holder, cell)
            "dining_dollars_spent" -> bindDollarsSpentReservationsCell(holder, cell)
            else -> Log.i("HomeAdapter", "Unsupported type of data at position $position")
        }
    }

    override fun getItemCount(): Int {
        return cells.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int {
        val cell = cells[position]
        return when (cell.type) {
            "dining_balance" -> DINING_BALANCE
            "dining_dollars_spent" -> DINING_DOLLARS_SPENT
            else -> NOT_SUPPORTED
        }
    }

    private fun bindDollarsSpentReservationsCell(holder: ViewHolder, cell: DiningInsightCell) {
        // Populate dining dollars spent card

    }

    private fun bindDiningBalanceCells(holder: ViewHolder, cell: DiningInsightCell) {
        val v = holder.view
        val diningBalances = cell.diningBalances
        val diningDollars = "$" + (diningBalances?.diningDollars ?: "0.00")
        val swipes = diningBalances?.regularVisits ?: 0
        val guestSwipes = diningBalances?.guestVisits ?: 0
        val tvDiningDollarsAmount = (v.findViewById<View>(R.id.dining_dollars_amount) as TextView)
        tvDiningDollarsAmount.text = diningDollars
        val tvRegularSwipesAmount = (v.findViewById<View>(R.id.swipes_amount) as TextView)
        tvRegularSwipesAmount.text = swipes.toString()
        val tvGuestSwipesAmount = (v.findViewById<View>(R.id.guest_swipes_amount) as TextView)
        tvGuestSwipesAmount.text = guestSwipes.toString()
    }

}