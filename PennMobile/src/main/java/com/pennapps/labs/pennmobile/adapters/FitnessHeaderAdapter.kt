package com.pennapps.labs.pennmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R

class FitnessHeaderAdapter(
    private val text: String,
) : RecyclerView.Adapter<FitnessHeaderAdapter.ViewHolder>() {
    class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        val headerView: TextView

        init {
            headerView = view.findViewById(R.id.fitness_section_title_text)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fitness_section_title, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.headerView.text = text
    }
}
