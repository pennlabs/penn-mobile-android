package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Poll
import com.pennapps.labs.pennmobile.classes.PollOption
import kotlinx.android.synthetic.main.poll_list_item.view.*
import kotlin.math.round


class PollOptionAdapter(private var pollOptions: ArrayList<PollOption>, private var poll: Poll) : RecyclerView.Adapter<PollOptionAdapter.ViewHolder>(){
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.poll_list_item, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pollOption = pollOptions[position]
        bindPollOption(holder, pollOption)
    }

    override fun getItemCount(): Int {
        return pollOptions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    private fun bindPollOption(holder: ViewHolder, pollOption: PollOption) {
        holder.itemView.tv_option?.text = pollOption.choice
        if(!poll.isVisible) {
            pollOptions.forEach {
                poll.totalVotes += it.voteCount
            }
            poll.isVisible = true
        }

        if(poll.isVisible) {
            holder.itemView.tv_votes?.text = "${pollOption.voteCount}"
            val votePercent = (pollOption.voteCount.div(poll.totalVotes.toDouble())) * 100
            holder.itemView.tv_percent?.text = String.format("%.2f%%", votePercent)
            holder.itemView.seek_bar?.progress = round(votePercent).toInt()
        }
    }
}