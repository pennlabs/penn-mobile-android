package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import kotlinx.android.synthetic.main.dining_balance_item.view.*
import kotlinx.android.synthetic.main.team_member.view.*
import kotlin.math.roundToInt

class DiningBalanceAdapter(private var values: ArrayList<Double>)
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
        // Only show dollar sign for dining dollars
        if (position == 0) {
            holder.view.dining_balance_value_tv?.text = "$${values[position]}"
        } else {
            holder.view.dining_balance_value_tv?.text = values[position].roundToInt().toString()
        }

        // Set title
        val title = when (position) {
            0 -> R.string.dining_dollars
            1 -> R.string.dining_swipes
            2 -> R.string.guest_swipes
            else -> null
        }
        if (title != null) holder.view.dining_balance_item_title?.text = title.toString()

        // Set image icon
        val imageId = when (position) {
            0 -> R.drawable.dining_balance_coin
            1 -> R.drawable.dining_balance_card
            2 -> R.drawable.dining_balance_friends
            else -> null
        }
        if (imageId != null) holder.view.dining_balance_item_iv?.setImageResource(imageId)
    }

    inner class DiningBalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}