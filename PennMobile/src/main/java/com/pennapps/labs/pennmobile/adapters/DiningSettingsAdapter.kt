package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.DiningHall
import kotlinx.android.synthetic.main.laundry_settings_child_item.view.laundry_favorite_switch
import kotlinx.android.synthetic.main.laundry_settings_child_item.view.laundry_room_name

class DiningSettingsAdapter(
    private var diningHalls: List<DiningHall>,
) : RecyclerView.Adapter<DiningSettingsAdapter.DiningSettingsViewHolder>() {
    private lateinit var mContext: Context
    private lateinit var sp: SharedPreferences

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiningSettingsViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.laundry_settings_child_item, parent, false)
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        return DiningSettingsViewHolder(view)
    }

    override fun getItemCount(): Int = diningHalls.count()

    override fun onBindViewHolder(
        holder: DiningSettingsViewHolder,
        position: Int,
    ) {
        val hall = diningHalls[position]
        holder.view.laundry_room_name?.text = hall.name
        val switch = holder.view.laundry_favorite_switch
        // set the switch to the correct on or off
        switch.isChecked = sp.getBoolean(hall.name, false)

        switch.setOnClickListener {
            val editor = sp.edit()
            editor.putBoolean(hall.name, switch.isChecked)
            editor.apply()

            Log.i("Dining", "hi yeah")
        }
    }

    inner class DiningSettingsViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
