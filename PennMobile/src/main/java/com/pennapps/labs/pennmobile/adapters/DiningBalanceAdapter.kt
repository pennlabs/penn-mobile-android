package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.DiningBalance
import kotlinx.android.synthetic.main.dining_balance_item.view.*

class DiningBalanceAdapter(private var balance: DiningBalance)
    : RecyclerView.Adapter<DiningBalanceAdapter.DiningBalanceViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiningBalanceViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.dining_balance_item, parent, false)
        return DiningBalanceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: DiningBalanceViewHolder, position: Int) {
        // Set value
        holder.view.dining_balance_value_tv?.text = when (position) {
            0 -> balance.diningDollars
            1 -> balance.regularVisits.toString()
            2 -> balance.guestVisits.toString()
            else -> null
        }

        // Set title
        holder.view.dining_balance_item_title?.text = when (position) {
            0 -> mContext.getString(R.string.dining_dollars)
            1 -> mContext.getString(R.string.dining_swipes)
            2 -> mContext.getString(R.string.guest_swipes)
            else -> null
        }

        // Set image icon
        val imageId = when (position) {
            0 -> R.drawable.dining_balance_coin
            1 -> R.drawable.dining_balance_card
            2 -> R.drawable.dining_balance_friends
            else -> null
        }

        // Set background color
        val color = when (position) {
            0 -> ContextCompat.getColor(mContext, R.color.balance_green)
            1 -> ContextCompat.getColor(mContext, R.color.balance_blue)
            2 -> ContextCompat.getColor(mContext, R.color.balance_purple)
            else -> ContextCompat.getColor(mContext, R.color.balance_green)
        }
        holder.view.dining_balance_cardview?.setCardBackgroundColor(color)
    }

    inner class DiningBalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}