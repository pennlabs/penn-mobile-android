package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.HomeCell

class HomeCellAdapter(private var cells: ArrayList<HomeCell>)
    : RecyclerView.Adapter<HomeCellAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    companion object {
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
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            DINING -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            CALENDAR -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            CALENDAR -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            NEWS -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            COURSES -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            LAUNDRY -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false)
                ViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cell = cells[position]
        Log.d("Home", cell.type)
        when (cell.type) {
            "reservations" -> bindHomeReservationsCell(holder, cell)
            "dining" -> {}
            "calendar" -> {}
            "news" -> {}
            "courses" -> {}
            "laundry" -> {}
            else -> throw IllegalArgumentException("Invalid type of data " + position)
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
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    private fun bindHomeReservationsCell(holder: ViewHolder, cell: HomeCell) {
        Log.d("Home", "you've got reservations")
    }
}