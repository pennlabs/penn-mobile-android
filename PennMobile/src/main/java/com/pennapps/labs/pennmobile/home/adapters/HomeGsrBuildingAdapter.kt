package com.pennapps.labs.pennmobile.home.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.HomeGsrBuildingBinding
import com.pennapps.labs.pennmobile.gsr.fragments.GsrTabbedFragment

class HomeGsrBuildingAdapter(
    private var buildings: ArrayList<String>,
) : RecyclerView.Adapter<HomeGsrBuildingAdapter.HomeGsrBuildingViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeGsrBuildingViewHolder {
        mContext = parent.context
        val itemBinding = HomeGsrBuildingBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HomeGsrBuildingViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: HomeGsrBuildingViewHolder,
        position: Int,
    ) {
        val building = buildings[position]
        holder.homeGsrBuildingTv.text = building
        if (building == "Huntsman Hall") {
            holder.homeGsrBuildingIv.setImageResource(R.drawable.huntsman)
        } else {
            holder.homeGsrBuildingIv.setImageResource(R.drawable.weigle)
        }
        holder.itemView.setOnClickListener {
            fragmentTransact(GsrTabbedFragment(), false)
        }
    }

    override fun getItemCount(): Int = buildings.size

    inner class HomeGsrBuildingViewHolder(
        itemBinding: HomeGsrBuildingBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val homeGsrBuildingIv: ImageView = itemBinding.homeGsrBuildingIv
        val homeGsrBuildingTv: TextView = itemBinding.homeGsrBuildingTv
    }

    private fun fragmentTransact(
        fragment: Fragment?,
        popBackStack: Boolean,
    ) {
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
