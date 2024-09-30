package com.pennapps.labs.pennmobile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.pennapps.labs.pennmobile.classes.DiningHall
import org.apache.commons.lang3.StringUtils

class MenuFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var toolBar: Toolbar

    private var mDiningHall: DiningHall? = null
    private var pageAdapter: PagerAdapter? = null

    inner class TabAdapter(
        fm: FragmentManager,
    ) : FragmentStatePagerAdapter(fm) {
        // for each meal: {name of station: arraylist of foods at the station}
        var foods: ArrayList<HashMap<String, ArrayList<String>>> = ArrayList()

        var headers: ArrayList<String> = ArrayList()
        var name: String? = null

        fun addTabs(hall: DiningHall?) {
            val menus = hall?.menus ?: ArrayList()
            name = hall?.name
            headers.add("HOURS")
            foods.add(HashMap()) // first menu is empty for dining hall info tab
            for (menu in menus) {
                val stations = HashMap<String, ArrayList<String>>()
                headers.add(menu.name)
                for (station in menu.stations) {
                    val foods = ArrayList<String>()
                    val foodItems = StringBuilder() // for design purposes
                    for (i in station.items.indices) {
                        val txt = station.items[i].title
                        foodItems.append(txt?.get(0)?.uppercaseChar())
                        foodItems.append(txt?.substring(1, txt.length))
                        if (i < station.items.size - 1) {
                            foodItems.append("\n")
                        }
                    }
                    foods.add(foodItems.toString())
                    stations[StringUtils.capitalize(station.name)] = foods
                }
                foods.add(stations)
            }
        }

        override fun getItem(position: Int): Fragment {
            val myFragment: Fragment
            if (position == 0) {
                myFragment = DiningInfoFragment()
                val args = Bundle()
                args.putParcelable("DiningHall", mDiningHall)
                args.putString(getString(R.string.menu_arg_name), name)
                myFragment.arguments = args
            } else {
                myFragment = MenuTab()
                val args = Bundle()
                args.putString(getString(R.string.menu_arg_name), name)
                args.putStringArrayList(getString(R.string.menu_arg_stations), ArrayList(foods[position].keys))
                val stations = foods[position]
                for (station in stations.keys) {
                    args.putStringArrayList(station, stations[station])
                }
                myFragment.arguments = args
            }
            return myFragment
        }

        override fun getPageTitle(position: Int): CharSequence = headers[position]

        override fun getCount(): Int = foods.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDiningHall = arguments?.getParcelable("DiningHall")
        mActivity = activity as MainActivity
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_menu, container, false)
        pageAdapter = TabAdapter(mActivity.supportFragmentManager)
        (pageAdapter as TabAdapter).addTabs(mDiningHall)
        val pager: ViewPager = v.findViewById(R.id.menu_pager)
        pager.adapter = pageAdapter
        v.setBackgroundColor(Color.WHITE)
        mActivity.addTabs(pageAdapter as TabAdapter, pager, true)
        return v
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        toolBar = mActivity.findViewById(R.id.toolbar)
        toolBar.visibility = View.VISIBLE
        mActivity.hideBottomBar()
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        inflater.inflate(R.menu.dining, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            mActivity.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mActivity.title = mDiningHall?.name
        if (mActivity.supportActionBar != null) {
            mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroyView() {
        if (view != null) {
            val pager: ViewPager = requireView().findViewById(R.id.menu_pager)
            pager.adapter = null
        }

        super.onDestroyView()
        mActivity.removeTabs()
        if (mActivity.supportActionBar != null) {
            mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        mActivity.supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity.removeTabs()
    }
}
