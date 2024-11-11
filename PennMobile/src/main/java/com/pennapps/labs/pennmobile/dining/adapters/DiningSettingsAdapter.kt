package com.pennapps.labs.pennmobile.dining.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.LaundrySettingsChildItemBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningHall

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
        val itemBinding = LaundrySettingsChildItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        return DiningSettingsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = diningHalls.count()

    override fun onBindViewHolder(
        holder: DiningSettingsViewHolder,
        position: Int,
    ) {
        val hall = diningHalls[position]
        holder.laundryRoomName.text = hall.name
        val switch = holder.laundryFavoriteSwitch
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
        itemBinding: LaundrySettingsChildItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val laundryRoomName = itemBinding.laundryRoomName
        val laundryFavoriteSwitch = itemBinding.laundryFavoriteSwitch
    }
}
