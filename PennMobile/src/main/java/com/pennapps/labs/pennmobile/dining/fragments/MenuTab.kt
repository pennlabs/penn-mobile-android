package com.pennapps.labs.pennmobile.dining.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.pennapps.labs.pennmobile.R

class MenuTab : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root =
            LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.WHITE)
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
            }

        if (stations.isEmpty()) return root

        // Station tabs
        val tabLayout =
            TabLayout(requireContext()).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                tabMode = TabLayout.MODE_SCROLLABLE
                setTabTextColors(Color.GRAY, Color.BLACK)
                setSelectedTabIndicatorColor(resources.getColor(R.color.color_primary, null))
            }
        stations.forEach { tabLayout.addTab(tabLayout.newTab().setText(it)) }
        root.addView(tabLayout)

        // Food items scroll area
        val scrollView =
            ScrollView(requireContext()).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                    )
            }
        val foodContainer =
            LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 24, 48, 24)
            }
        scrollView.addView(foodContainer)
        root.addView(scrollView)

        fun showStation(station: String) {
            foodContainer.removeAllViews()
            val foods = stationInfo[station] ?: return
            // Each entry in foods is a newline-separated string of food items
            foods.forEach { foodBlock ->
                foodBlock.split("\n").filter { it.isNotBlank() }.forEach { item ->
                    foodContainer.addView(
                        TextView(requireContext()).apply {
                            text = item
                            textSize = 14f
                            setTextColor(Color.DKGRAY)
                            setPadding(0, 12, 0, 12)
                        },
                    )
                }
            }
        }

        // Show first station by default
        showStation(stations[0])

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    showStation(stations[tab.position])
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            },
        )

        return root
    }
}
