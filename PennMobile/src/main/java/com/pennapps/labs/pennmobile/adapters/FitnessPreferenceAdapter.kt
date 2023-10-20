package com.pennapps.labs.pennmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.FitnessAdapterDataModel

class FitnessPreferenceAdapter(private val dataModel: FitnessAdapterDataModel)
    : RecyclerView.Adapter<FitnessPreferenceAdapter.ViewHolder>() {
    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val v : View
        val imv : ImageView
        val textView : TextView

        init {
            v = view
            imv = v.findViewById(R.id.fitness_preference_select)
            textView = v.findViewById(R.id.fitness_preference_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fitness_preference_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataModel.getTot()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = dataModel.getRoomAll(position)
        holder.textView.text = room.roomName

        val rid : Int = room.roomId!!

        if (dataModel.isFavorite(rid)) {
            holder.imv.visibility = View.VISIBLE
        }

        holder.v.setOnClickListener {
            val b = dataModel.flipState(rid)
            if (b) {
                holder.imv.visibility = View.VISIBLE
            } else {
                holder.imv.visibility = View.INVISIBLE
            }
        }
    }
}