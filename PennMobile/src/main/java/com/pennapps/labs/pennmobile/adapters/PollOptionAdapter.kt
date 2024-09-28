package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Poll
import com.pennapps.labs.pennmobile.classes.PollOption
import com.pennapps.labs.pennmobile.databinding.PollListItemBinding
import kotlin.math.abs
import kotlin.math.round

class PollOptionAdapter(
    private var pollOptions: ArrayList<PollOption>,
    private var poll: Poll,
) : RecyclerView.Adapter<PollOptionAdapter.PollHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PollHolder {
        mContext = parent.context
        val itemBinding = PollListItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        val pollHolder = PollHolder(itemBinding)
        return pollHolder
    }

    override fun onBindViewHolder(
        holder: PollHolder,
        position: Int,
    ) {
        val pollOption = pollOptions[position]
        bindPollOption(holder, pollOption)
    }

    override fun getItemCount(): Int = pollOptions.size

    inner class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int = 0

    private fun bindPollOption(
        holder: PollHolder,
        pollOption: PollOption,
    ) {
        holder.tv_option.text = pollOption.choice

        var startX: Float? = null
        var startY: Float? = null
        holder.seek_bar.setOnTouchListener { v, event ->
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
            holder.tv_option.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.tv_percent.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.tv_votes.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.seek_bar.progressDrawable = mContext.getDrawable(R.drawable.poll_track_selected)
            holder.card_view.foreground = mContext.getDrawable(R.drawable.card_view_border)
        } else {
            holder.tv_option.setTextColor(Color.parseColor("#13284B"))
            holder.tv_percent.setTextColor(Color.parseColor("#13284B"))
            holder.tv_votes.setTextColor(Color.parseColor("#13284B"))
            holder.seek_bar.progressDrawable = mContext.getDrawable(R.drawable.poll_track)
            holder.card_view.foreground = null
        }
        if (poll.isVisible) {
            holder.tv_votes.text = "${pollOption.voteCount}"
            val votePercent = (pollOption.voteCount.div(poll.totalVotes.toDouble())) * 100
            holder.tv_percent.text = String.format("%.2f%%", votePercent)
            holder.seek_bar.progress = round(votePercent).toInt()
            holder.seek_bar.setOnTouchListener { v, event -> true }
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
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD) // =5
    }

    class PollHolder(
        itemBinding: PollListItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        internal var tv_option: TextView = itemBinding.tvOption
        internal var tv_percent: TextView = itemBinding.tvPercent
        internal var seek_bar: SeekBar = itemBinding.seekBar
        internal var card_view: CardView = itemBinding.cardView
        internal var tv_votes: TextView = itemBinding.tvVotes
    }

    companion object {
        private const val CLICK_ACTION_THRESHOLD: Int = 200
    }
}
