package com.pennapps.labs.pennmobile.dining.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils

class MenuFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var viewModel: DiningViewModel

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
        viewModel = ViewModelProvider(requireActivity())[DiningViewModel::class.java]
        setHasOptionsMenu(true)
        viewModel.refreshData()
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

        val tabLayout = v.findViewById<TabLayout>(R.id.dining_tab_layout)
        tabLayout.setupWithViewPager(pager)
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)

        return v
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        WindowInsetsControllerCompat(requireActivity().window, requireView()).isAppearanceLightStatusBars = false

        val localToolbar = view.findViewById<MaterialToolbar>(R.id.dining_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(localToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        localToolbar.navigationIcon?.setTint(Color.WHITE)

        // set image
        val imageView = view.findViewById<ImageView>(R.id.dining_header_image)
        imageView.setImageResource(mDiningHall?.image ?: 0)

        // set title
        view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar).title = mDiningHall?.name
        view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar).setExpandedTitleColor(Color.WHITE)
        view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar).setCollapsedTitleTextColor(Color.WHITE)

        // set toolbar buttons
        view.findViewById<ImageButton>(R.id.dining_location).setOnClickListener {
            val location =
                when (mDiningHall?.name) {
                    "Accenture Café" -> "Towne Building"
                    "Cafe West" -> "Gutmann College House"
                    "Falk Kosher Dining" -> "Penn Hillel"
                    "Joe's Café" -> "3620 Locust Walk"
                    "McClelland Express" -> "3700 Spruce Street"
                    "Pret a Manger Locust", "Pret a Manger MBA" -> "3730 Walnut St, Philadelphia, PA 19104"
                    else -> mDiningHall?.name
                }
            val diningHallMapUrl = "https://maps.google.com/?q=$location"
            val intent = Intent(Intent.ACTION_VIEW, diningHallMapUrl.toUri())
            startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.dining_website).setOnClickListener {
            val website =
                when (mDiningHall?.name) {
                    "Falk Kosher Dining" -> "Falk Dining Commons"
                    "Accenture Café" -> "Accenture Cafe"
                    "Joe's Café" -> "Joes Cafe"
                    "English House" -> "kings court english house"
                    "McClelland Express" -> "pdss"
                    "Pret a Manger Locust" -> "Pret a Manger Lower"
                    "Pret a Manger MBA" -> "Pret a Manger Upper"
                    else -> mDiningHall?.name
                }
            val formattedDiningName = website?.lowercase()?.replace(" ", "-")
            val diningHallMenuUrl = "https://university-of-pennsylvania.cafebonappetit.com/cafe/$formattedDiningName/"
            val intent = Intent(Intent.ACTION_VIEW, diningHallMenuUrl.toUri())
            startActivity(intent)
        }

        val favoriteButton = view.findViewById<ImageButton>(R.id.favorite_dining)

        fun updateFavoriteButton(isFavourite: Boolean) {
            val icon =
                if (isFavourite) {
                    R.drawable.ic_star_24dp
                } else {
                    R.drawable.ic_star_border_24dp
                }
            favoriteButton.setImageResource(icon)
            favoriteButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favouriteDiningHallIds.collect { favoriteIds ->
                updateFavoriteButton(favoriteIds.contains(mDiningHall?.id))
            }
        }

        favoriteButton.setOnClickListener {
            mDiningHall?.let { hall ->
                viewModel.toggleFavourite(hall)
                updateFavoriteButton(viewModel.isFavourite(hall))
            }
        }
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
        WindowInsetsControllerCompat(requireActivity().window, requireView()).isAppearanceLightStatusBars = true

        super.onDestroyView()
        mActivity.supportActionBar?.hide()
        mActivity.showBottomBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity.removeTabs()
    }
}
