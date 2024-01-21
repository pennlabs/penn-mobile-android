package com.pennapps.labs.pennmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Tab
import kotlinx.android.synthetic.main.dynamic_tab_item.view.*

class DynamicTabsAdapter(private val tabs : List<Tab>) : RecyclerView.Adapter<DynamicTabsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)  {

        init {
            // Define click listener for the ViewHolder's View
        }
        fun bindTab(tab: Tab) {
            view.tvDynamicTab.text = tab.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dynamic_tab_item, parent, false))
    }

    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView
        holder.bindTab(tabs[position])
    }
}