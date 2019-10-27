package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.HomeCell
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.home_base_card.view.*

class HomeAdapter(private var cells: ArrayList<HomeCell>)
    : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    companion object {
        private const val NOT_SUPPORTED = -1
        private const val RESERVATIONS = 0
        private const val DINING = 1
        private const val CALENDAR = 2
        private const val NEWS = 3
        private const val COURSES = 4
        private const val LAUNDRY = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        return when (viewType) {
            RESERVATIONS -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            DINING -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            CALENDAR -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            NEWS -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            COURSES -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            LAUNDRY -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_base_card, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cell = cells[position]
        when (cell.type) {
            "reservations" -> bindHomeReservationsCell(holder, cell)
            "dining" -> bindDiningCell(holder, cell)
            "calendar" -> bindCalendarCell(holder, cell)
            "news" -> bindNewsCell(holder, cell)
            "courses" -> bindCoursesCell(holder, cell)
            "laundry" -> bindLaundryCell(holder, cell)
            else -> Log.d("HomeAdapter", "Unsupported type of data at position " + position)
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
            "reservations" -> RESERVATIONS
            "dining" -> DINING
            "calendar" -> CALENDAR
            "news" -> NEWS
            "courses" -> COURSES
            "laundry" -> LAUNDRY
            else -> NOT_SUPPORTED
        }
    }

    private fun bindHomeReservationsCell(holder: ViewHolder, cell: HomeCell) {
        val reservations = cell.reservations
        holder.itemView.home_card_title.text = "Upcoming Reservations"
        holder.itemView.home_card_subtitle.text = "GSR RESERVATIONS"
        holder.itemView.home_card_rv.layoutManager = LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false)

        //val divider = DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL)
        //holder.itemView.home_card_rv.addItemDecoration(divider)
        holder.itemView.home_card_rv.adapter = GsrReservationsAdapter(ArrayList(reservations))

    }

    private fun bindDiningCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Favorites"
        holder.itemView.home_card_subtitle.text = "DINING HALLS"
    }

    private fun bindNewsCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "News"
        holder.itemView.home_card_subtitle.text = "NEWS"
    }

    private fun bindCalendarCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Upcoming Events"
        holder.itemView.home_card_subtitle.text = "UNIVERSITY NOTIFICATIONS"
    }

    private fun bindCoursesCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Today's schedule"
        holder.itemView.home_card_subtitle.text = "COURSE SCHEDULE"
    }

    private fun bindLaundryCell(holder: ViewHolder, cell: HomeCell) {
        holder.itemView.home_card_title.text = "Laundry"
        holder.itemView.home_card_subtitle.text = "LAUNDRY"
    }
}