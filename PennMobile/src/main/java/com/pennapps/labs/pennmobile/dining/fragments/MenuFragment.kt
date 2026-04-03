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
import android.widget.PopupMenu
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
import com.pennapps.labs.pennmobile.dining.classes.VenueInterval
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils

class MenuFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var viewModel: DiningViewModel

    private var mDiningHall: DiningHall? = null
    private var pageAdapter: PagerAdapter? = null

    private var availableDays: List<VenueInterval> = emptyList()
    private var selectedDayIndex: Int = 0
    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var dateHoursRow: View

    inner class MenuTabAdapter(
        fm: FragmentManager,
        private val menus: List<DiningHall.Menu>,
        private val hallName: String?,
    ) : FragmentStatePagerAdapter(fm) {
        var foods: ArrayList<HashMap<String, ArrayList<String>>> = ArrayList()

        var headers: ArrayList<String> = ArrayList()
        var name: String? = null

        init {
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
            val myFragment = MenuTab()
            val args = Bundle()
            args.putString(getString(R.string.menu_arg_name), name)
            args.putStringArrayList(getString(R.string.menu_arg_stations), ArrayList(foods[position].keys))
            val stations = foods[position]
            for (station in stations.keys) {
                args.putStringArrayList(station, stations[station])
            }
            myFragment.arguments = args
            return myFragment
        }

        override fun getPageTitle(position: Int): CharSequence = headers[position]

        override fun getCount(): Int = foods.size
    }

    /**
     * Adapter used when a dining hall has no menu data for a specific date.
     */
    inner class NoMenuDataAdapter(
        fm: FragmentManager,
    ) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = NoMenuDataFragment()

        override fun getPageTitle(position: Int): CharSequence = "NO MENU"

        override fun getCount(): Int = 1
    }

    class NoMenuDataFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View =
            TextView(requireContext()).apply {
                text = "No menu data available for this date."
                setTextColor(Color.GRAY)
                textSize = 15f
                gravity = android.view.Gravity.CENTER
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                setBackgroundColor(Color.WHITE)
            }
    }

    /**
     * Adapter used when the hall has no menu (i.e. joe's cafe)
     */
    inner class HoursOnlyTabAdapter(
        fm: FragmentManager,
    ) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            // Pass venue data directly without parceling to avoid venue being null
            val fragment = DiningInfoFragment()
            fragment.mDiningHall = mDiningHall
            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence = "HOURS"

        override fun getCount(): Int = 1
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
        pager = v.findViewById(R.id.menu_pager)
        tabLayout = v.findViewById(R.id.dining_tab_layout)
        dateHoursRow = v.findViewById(R.id.dining_date_hours_row)
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        v.setBackgroundColor(Color.WHITE)

        val hasAnyMenus = MENU_HALLS.contains(mDiningHall?.name)

        if (hasAnyMenus) {
            // date & hours row, tabs = meal types
            dateHoursRow.visibility = View.VISIBLE
            mDiningHall?.let { viewModel.fetchMenusForWeek(it) }

            // refresh tabs whenever the selected date's data arrives
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.menusByDate.collect { menusByDate ->
                    val dateStr = selectedDateString()
                    val menus = menusByDate[dateStr]
                    rebuildMenuTabs(menus ?: emptyList())
                }
            }
        } else {
            // No menu data: hide date picker row, only show HOURS tab
            dateHoursRow.visibility = View.GONE
            val adapter = HoursOnlyTabAdapter(mActivity.supportFragmentManager)
            pageAdapter = adapter
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)
            tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        }
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

        // Date picker + hours row (only shows up when hall has menus)
        val allDays =
            (mDiningHall?.venue?.allHours() ?: emptyList())
                .filter { it.meals.isNotEmpty() }
        val todayStr =
            org.joda.time.LocalDate
                .now()
                .toString("yyyy-MM-dd")

        // append td's menu if missing
        availableDays =
            if (allDays.none { it.date == todayStr }) {
                val todayInterval = VenueInterval().also { it.date = todayStr }
                listOf(todayInterval) + allDays
            } else {
                allDays
            }
        selectedDayIndex = findTodayIndex()

        val datePickerButton = view.findViewById<Button>(R.id.dining_date_picker_button)
        val hoursText = view.findViewById<TextView>(R.id.dining_hours_text)

        fun renderDateRow(dayIndex: Int) {
            val day = availableDays.getOrNull(dayIndex)
            datePickerButton.text = formatDayLabel(dayIndex) + " ▼"
            hoursText.text = day?.meals?.joinToString(" | ") { meal ->
                val o = meal.open?.let { meal.getFormattedHour(it) } ?: ""
                val c = meal.close?.let { meal.getFormattedHour(it) } ?: ""
                "$o–$c"
            } ?: "Closed"
        }

        datePickerButton.setOnClickListener { anchor ->
            val popup = PopupMenu(mActivity, anchor)
            availableDays.forEachIndexed { index, _ ->
                popup.menu.add(0, index, index, formatDayLabel(index))
            }
            popup.setOnMenuItemClickListener { item ->
                selectedDayIndex = item.itemId
                renderDateRow(selectedDayIndex)
                val dateStr = selectedDateString()
                val cached = viewModel.menusByDate.value[dateStr]
                if (!cached.isNullOrEmpty()) {
                    rebuildMenuTabs(cached)
                } else {
                    rebuildMenuTabs(emptyList())
                }
                true
            }
            popup.show()
        }

        renderDateRow(selectedDayIndex)

        mActivity.hideBottomBar()
    }

    private fun rebuildMenuTabs(menus: List<DiningHall.Menu>) {
        if (menus.isEmpty()) {
            // No menu data fetched yet for this date — show a placeholder tab
            val adapter = NoMenuDataAdapter(mActivity.supportFragmentManager)
            pageAdapter = adapter
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)
            tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        } else {
            val adapter = MenuTabAdapter(mActivity.supportFragmentManager, menus, mDiningHall?.name)
            pageAdapter = adapter
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)
            tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        }
    }

    private fun selectedDateString(): String =
        availableDays.getOrNull(selectedDayIndex)?.date
            ?: org.joda.time.LocalDate
                .now()
                .toString("yyyy-MM-dd")

    private fun findTodayIndex(): Int {
        val today =
            org.joda.time.LocalDate
                .now()
        availableDays.forEachIndexed { i, day ->
            if (day.date == today.toString("yyyy-MM-dd")) {
                return i
            }
        }
        return 0
    }

    private fun formatDayLabel(index: Int): String {
        if (index == findTodayIndex()) return "Today"
        val day = availableDays.getOrNull(index) ?: return ""
        val dt =
            org.joda.time.LocalDate
                .parse(day.date) // no timezone
        return dt.dayOfWeek().asText + ", " +
            dt.monthOfYear().asShortText + " " +
            dt.dayOfMonth().asText
    }

    companion object {
        // Halls that have full food menus
        val MENU_HALLS =
            listOf(
                "Hill House",
                "Lauder College House",
                "English House",
                "Falk Kosher Dining",
                "1920 Commons",
                "Accenture Café",
                "Joe's Café",
                "Houston Market",
            )
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
