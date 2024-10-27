package com.pennapps.labs.pennmobile.home.adapters

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
import com.pennapps.labs.pennmobile.home.classes.Poll
import com.pennapps.labs.pennmobile.home.classes.PollOption
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
        holder.tvOption.text = pollOption.choice

        var startX: Float? = null
        var startY: Float? = null
        holder.seekBar.setOnTouchListener { v, event ->
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
            holder.tvOption.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.tvPercent.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.tvVotes.setTextColor(mContext.resources.getColor(R.color.color_secondary))
            holder.seekBar.progressDrawable = mContext.getDrawable(R.drawable.poll_track_selected)
            holder.cardView.foreground = mContext.getDrawable(R.drawable.card_view_border)
        } else {
            holder.tvOption.setTextColor(Color.parseColor("#13284B"))
            holder.tvPercent.setTextColor(Color.parseColor("#13284B"))
            holder.tvVotes.setTextColor(Color.parseColor("#13284B"))
            holder.seekBar.progressDrawable = mContext.getDrawable(R.drawable.poll_track)
            holder.cardView.foreground = null
        }
        if (poll.isVisible) {
            holder.tvVotes.text = "${pollOption.voteCount}"
            val votePercent = (pollOption.voteCount.div(poll.totalVotes.toDouble())) * 100
            holder.tvPercent.text = String.format("%.2f%%", votePercent)
            holder.seekBar.progress = round(votePercent).toInt()
            holder.seekBar.setOnTouchListener { v, event -> true }
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
        internal var tvOption: TextView = itemBinding.tvOption
        internal var tvPercent: TextView = itemBinding.tvPercent
        internal var seekBar: SeekBar = itemBinding.seekBar
        internal var cardView: CardView = itemBinding.cardView
        internal var tvVotes: TextView = itemBinding.tvVotes
    }

    companion object {
        private const val CLICK_ACTION_THRESHOLD: Int = 200
    }
}
