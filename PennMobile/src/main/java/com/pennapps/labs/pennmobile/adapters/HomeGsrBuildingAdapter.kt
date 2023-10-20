package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.pennapps.labs.pennmobile.GsrTabbedFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import kotlinx.android.synthetic.main.home_gsr_building.view.*

class HomeGsrBuildingAdapter(private var buildings: ArrayList<String>)
    : RecyclerView.Adapter<HomeGsrBuildingAdapter.HomeGsrBuildingViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeGsrBuildingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_gsr_building, parent, false)
        mContext = parent.context
        return HomeGsrBuildingViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeGsrBuildingViewHolder, position: Int) {
        val building = buildings[position]

        holder.itemView.home_gsr_building_tv.text = building
        holder.itemView.home_gsr_building_iv
        if (building == "Huntsman Hall") {
            holder.itemView.home_gsr_building_iv.setImageResource(R.drawable.huntsman)
        } else {
            holder.itemView.home_gsr_building_iv.setImageResource(R.drawable.weigle)
        }
        holder.itemView.setOnClickListener {
            fragmentTransact(GsrTabbedFragment(), false)
        }

    }

    override fun getItemCount(): Int {
        return buildings.size
    }

    inner class HomeGsrBuildingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    private fun fragmentTransact(fragment: Fragment?, popBackStack: Boolean) {
        if (fragment != null) {
            if (mContext is FragmentActivity) {
                try {
                    val activity = mContext as FragmentActivity
                    val fragmentManager = activity.supportFragmentManager
                    if (popBackStack) {
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    (activity as MainActivity).setTab(MainActivity.GSR_ID)
                } catch (e: IllegalStateException) {
                    Log.e("HomeAdapter", e.toString())
                }

            }
        }
    }
}