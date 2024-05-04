package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Poll
import com.pennapps.labs.pennmobile.classes.PollOption
import kotlinx.android.synthetic.main.poll_list_item.view.*
import kotlin.math.abs
import kotlin.math.round

class PollOptionAdapter(private var pollOptions: ArrayList<PollOption>, private var poll: Poll) : RecyclerView.Adapter<PollOptionAdapter.ViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        mContext = parent.context
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.poll_list_item, parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
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

    private fun bindPollOption(
        holder: ViewHolder,
        pollOption: PollOption,
    ) {
        holder.itemView.tv_option?.text = pollOption.choice

        var startX: Float? = null
        var startY: Float? = null
        holder.itemView.seek_bar?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    if (startX != null && startY != null && isAClick(startX!!, endX, startY!!, endY)) {
                        poll.selectOption(pollOption)
                        notifyDataSetChanged()
                    }
                }
            }
            true
        }
        if (pollOption.selected) {
            holder.itemView.tv_option?.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.itemView.tv_percent?.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.itemView.tv_votes?.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.itemView.seek_bar?.progressDrawable = mContext.getDrawable(R.drawable.poll_track_selected)
            holder.itemView.card_view?.foreground = mContext.getDrawable(R.drawable.card_view_border)
        } else {
            holder.itemView.tv_option?.setTextColor(Color.parseColor("#13284B"))
            holder.itemView.tv_percent?.setTextColor(Color.parseColor("#13284B"))
            holder.itemView.tv_votes?.setTextColor(Color.parseColor("#13284B"))
            holder.itemView.seek_bar?.progressDrawable = mContext.getDrawable(R.drawable.poll_track)
            holder.itemView.card_view?.foreground = null
        }
        if (poll.isVisible) {
            holder.itemView.tv_votes?.text = "${pollOption.voteCount}"
            val votePercent = (pollOption.voteCount.div(poll.totalVotes.toDouble())) * 100
            holder.itemView.tv_percent?.text = String.format("%.2f%%", votePercent)
            holder.itemView.seek_bar?.progress = round(votePercent).toInt()
            holder.itemView.seek_bar?.setOnTouchListener { v, event -> true }
        }
    }

    private fun isAClick(
        startX: Float,
        endX: Float,
        startY: Float,
        endY: Float,
    ): Boolean {
        val differenceX = abs(startX - endX)
        val differenceY = abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD /* =5 */ || differenceY > CLICK_ACTION_THRESHOLD)
    }

    companion object {
        private const val CLICK_ACTION_THRESHOLD: Int = 200
    }
}
