package com.pennapps.labs.pennmobile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.adapters.MenuAdapter
import kotlinx.android.synthetic.main.fragment_menu_tab.view.*
import java.util.*

class MenuTab : Fragment() {

    var menuParent: LinearLayout? = null
    var meal: String? = null
    var stationInfo = HashMap<String, List<String>?>() // {station name: foods}
    private lateinit var stations: ArrayList<String>
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        name = args?.getString(getString(R.string.menu_arg_name), "Dining Hall") ?: "Dining Hall"
        meal = args?.getString(getString(R.string.menu_arg_meal), "Meal")
        stations = args?.getStringArrayList(getString(R.string.menu_arg_stations)) ?: ArrayList()
        for (station in stations) {
            stationInfo[station] = args?.getStringArrayList(station)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_menu_tab, container, false)
        val elv : ExpandableListView = v.station_list
        elv.setFooterDividersEnabled(true)
        elv.addFooterView(View(elv.context))
        elv.setAdapter(MenuAdapter(activity, stations, stationInfo))
        v.setBackgroundColor(Color.WHITE)
        return v
    }
}