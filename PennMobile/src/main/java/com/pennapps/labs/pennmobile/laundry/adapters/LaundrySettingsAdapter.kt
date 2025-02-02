package com.pennapps.labs.pennmobile.laundry.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.laundry.LaundryViewModel
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoomSimple

/**
 * Created by Jackie on 2017-10-13.
 * Modified by Aaron on 2024-03-20.
 */
class LaundrySettingsAdapter(
    private val mContext: Context,
    private val dataModel: LaundryViewModel,
) : BaseExpandableListAdapter() {
    private val switches: MutableList<SwitchCompat> = ArrayList()

    init {
        dataModel.setToggled()
    }

    override fun getGroupCount(): Int = dataModel.getGroupCount()

    override fun getChildrenCount(i: Int): Int = dataModel.getChildrenCount(i)

    override fun getGroup(i: Int): Any = dataModel.getGroup(i)

    override fun getChild(
        i: Int,
        i1: Int,
    ): Any = dataModel.getChild(i, i1)

    override fun getGroupId(i: Int): Long = i.toLong()

    override fun getChildId(
        i: Int,
        i1: Int,
    ): Long = i1.toLong()

    override fun hasStableIds(): Boolean = false

    // view for the laundry buildings
    override fun getGroupView(
        i: Int,
        b: Boolean,
        origView: View?,
        viewGroup: ViewGroup,
    ): View {
        val laundryHallName = getGroup(i) as String
        val view: View =
            if (origView == null) {
                val inflater =
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.laundry_settings_parent_item, null)
            } else {
                origView
            }
        val textView = view.findViewById<TextView>(R.id.laundry_building_name)
        textView.text = laundryHallName
        val imageView = view.findViewById<ImageView>(R.id.laundry_building_dropdown)
        val buildingSwitch = view.findViewById<SwitchCompat>(R.id.laundry_building_favorite_switch)

        // if there is only one laundry room in the building, don't have dropdown
        if (dataModel.getRooms(laundryHallName)!!.size == 1) {
            buildingSwitch.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            val laundryRoom = dataModel.getRooms(laundryHallName)!![0]
            val roomId: Int = laundryRoom.id!!
            // set the Switch to the correct on or off
            buildingSwitch.isChecked = dataModel.isChecked(roomId)

            // add the switch to the list - to aid with disabling
            if (!switches.contains(buildingSwitch)) {
                switches.add(buildingSwitch)
            }

            // max number of rooms
            if (dataModel.isFull()) {
                buildingSwitch.isEnabled = buildingSwitch.isChecked
            }
            buildingSwitch.setOnClickListener {
                if (dataModel.toggle(roomId)) {
                    updateSwitches()
                }
            }
        } else {
            buildingSwitch.visibility = View.GONE
            imageView.setImageResource(R.drawable.ic_expand)
            imageView.visibility = View.VISIBLE
        }

        // if expanded
        if (b) {
            imageView.setImageResource(R.drawable.ic_collapse)
        }
        return view
    }

    override fun getChildView(
        i: Int,
        i1: Int,
        b: Boolean,
        origView: View?,
        viewGroup: ViewGroup,
    ): View {
        val laundryRoom = getChild(i, i1) as LaundryRoomSimple
        val view: View =
            if (origView == null) {
                val inflater =
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.laundry_settings_child_item, null)
            } else {
                origView
            }
        val textView = view.findViewById<TextView>(R.id.laundry_room_name)
        val name = laundryRoom.name
        textView.text = name
        val favoriteSwitch = view.findViewById<SwitchCompat>(R.id.laundry_favorite_switch)

        val roomId: Int = laundryRoom.id!!

        // set the Switch to the correct on or off
        favoriteSwitch.isChecked = dataModel.isChecked(roomId)

        // add the switch to the list - to aid with disabling
        if (!switches.contains(favoriteSwitch)) {
            switches.add(favoriteSwitch)
        }

        // max number of rooms
        if (dataModel.isFull()) {
            favoriteSwitch.isEnabled = favoriteSwitch.isChecked
        }
        favoriteSwitch.setOnClickListener {
            if (dataModel.toggle(roomId)) {
                updateSwitches()
            }
        }
        return view
    }

    override fun isChildSelectable(
        i: Int,
        i1: Int,
    ): Boolean = false

    private fun updateSwitches() {
        if (dataModel.isFull()) {
            val iter: MutableIterator<SwitchCompat> = switches.iterator()
            while (iter.hasNext()) {
                val nextSwitch = iter.next()
                if (!nextSwitch.isChecked) {
                    nextSwitch.isEnabled = false
                }
            }
        } else {
            val iter: MutableIterator<SwitchCompat> = switches.iterator()
            while (iter.hasNext()) {
                val nextSwitch = iter.next()
                nextSwitch.isEnabled = true
            }
        }
    }
}
