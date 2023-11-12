package com.pennapps.labs.pennmobile

import android.content.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.classes.HomeCellInfo
import com.pennapps.labs.pennmobile.classes.PollCell
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentHomeBinding
import com.pennapps.labs.pennmobile.utils.Utils
import com.pennapps.labs.pennmobile.utils.Utils.getSha256Hash
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        LocalBroadcastManager
            .getInstance(mActivity)
            .registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "11")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        view.home_cells_rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout
            .setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout
            .setOnRefreshListener { getHomePage() }

        initAppBar(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHomePage()
    }

    private fun getHomePage() {

        //displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionHome.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessage.text = getString(R.string.internet_error)
            binding.homeCellsRv.setPadding(0, 90, 0, 0)
            binding.internetConnectionHome.visibility = View.VISIBLE
            binding.homeRefreshLayout.isRefreshing = false
            loadingPanel?.visibility = View.GONE
            return
        } else {
            binding.internetConnectionHome.visibility = View.GONE
            binding.homeCellsRv.setPadding(0, 0, 0, 0)
        }

        // get API data
        val homepageCells = mutableListOf<HomeCell>()

        for (i in 1..7) {
            homepageCells.add(HomeCell())
        }

        // number of cells loaded
        var loaded = 0

        val studentLife = MainActivity.studentLifeInstance
        OAuth2NetworkManager(mActivity).getAccessToken {
            val sp = sharedPreferences
            val deviceID = OAuth2NetworkManager(mActivity).getDeviceId()
            val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
            Log.i("HomeFragment", bearerToken)
            if (bearerToken != "Bearer ") {
                val totalCells = 6

                val idHash = getSha256Hash(deviceID)
                studentLife.browsePolls(bearerToken, idHash).subscribe({ poll ->
                    if (poll.size > 0) {
                        mActivity.runOnUiThread {
                            val pollCell = PollCell(poll[0])
                            pollCell.poll.options.forEach { pollCell.poll.totalVotes += it.voteCount }
                            homepageCells[0] = pollCell
                        }
                    }

                    if (++loaded == totalCells) {
                        mActivity.runOnUiThread {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }

                    Log.i("HomeFragment", "polls success $loaded")

                }, { throwable ->
                    Log.e("Poll", "Error retrieving polls", throwable)

                    if (++loaded >= totalCells) {
                        mActivity.runOnUiThread {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }

                    Log.i("HomeFragment", "polls $loaded")
                })

                studentLife.news.subscribe({ article ->
                    mActivity.runOnUiThread {
                        val newsCell = HomeCell()
                        newsCell.info = HomeCellInfo()
                        newsCell.info?.article = article
                        newsCell.type = "news"
                        homepageCells[3] = newsCell

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "news $loaded")
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load news", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "news $loaded")

                    }
                })

                studentLife.getDiningPreferences(bearerToken).subscribe({ preferences ->
                    mActivity.runOnUiThread {
                        val list = preferences.preferences
                        val venues = mutableListOf<Int>()
                        val diningCell = HomeCell()
                        diningCell.type = "dining"
                        val diningCellInfo = HomeCellInfo()
                        if (list?.isEmpty() == true) {
                            venues.add(593)
                            venues.add(1442)
                            venues.add(636)
                        } else {
                            list?.forEach({
                                it.id?.let { it1 -> venues.add(it1) }
                            })

                        }
                        diningCellInfo.venues = venues
                        diningCell.info = diningCellInfo
                        homepageCells[4] = diningCell

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }

                        Log.i("HomeFragment", "dining $loaded")
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load Dining", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "dining $loaded")
                    }
                })

                studentLife.calendar.subscribe({ events ->
                    mActivity.runOnUiThread {
                        val calendar = HomeCell()
                        calendar.type = "calendar"
                        calendar.events = events
                        homepageCells[1] = calendar
                        val gsrBookingCell = HomeCell()
                        gsrBookingCell.type = "gsr_booking"
                        gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "Weigle")
                        homepageCells[5] = gsrBookingCell

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "calendar $loaded")
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load calendar", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "calendar $loaded")
                    }
                })

                studentLife.getLaundryPref(bearerToken).subscribe({ preferences ->
                    mActivity.runOnUiThread {
                        val venues = mutableListOf<Int>()
                        val laundryCell = HomeCell()
                        laundryCell.type = "laundry"
                        val laundryCellInfo = HomeCellInfo()
                        if (preferences?.isEmpty() == false) {
                            laundryCellInfo.roomId = preferences[0]
                        }
                        laundryCell.info = laundryCellInfo
                        homepageCells[6] = laundryCell

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "laundry $loaded")
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load laundry", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "laundry $loaded")
                    }
                })

                studentLife.validPostsList(bearerToken).subscribe({ post ->
                    if (post.size >= 1) { //there exists a post
                        mActivity.runOnUiThread {
                            val postCell = HomeCell()
                            postCell.info = HomeCellInfo()
                            postCell.type = "post"
                            postCell.info?.post = post[0]
                            homepageCells[2] = postCell
                        }
                    }

                    if (++loaded >= totalCells) {
                        binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                        loadingPanel?.visibility = View.GONE
                        binding.internetConnectionHome.visibility = View.GONE
                        binding.homeRefreshLayout.isRefreshing = false
                    }
                    Log.i("HomeFragment", "posts $loaded")

                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load posts", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                        Log.i("HomeFragment", "posts $loaded")
                    }

                })
            } else {
                val totalCells = 2

                studentLife.calendar.subscribe({ events ->
                    mActivity.runOnUiThread {
                        val calendar = HomeCell()
                        calendar.type = "calendar"
                        calendar.events = events
                        homepageCells.add(0, calendar)

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load Home page", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }
                })

                studentLife.news.subscribe({ article ->
                    mActivity.runOnUiThread {
                        val newsCell = HomeCell()
                        newsCell.info = HomeCellInfo()
                        newsCell.info?.article = article
                        newsCell.type = "news"
                        homepageCells.add(homepageCells.size, newsCell)
                        val gsrBookingCell = HomeCell()
                        gsrBookingCell.type = "gsr_booking"
                        gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "Weigle")
                        homepageCells.add(homepageCells.size, gsrBookingCell)

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }
                }, { throwable ->
                    mActivity.runOnUiThread {
                        Log.e("Home", "Could not load Home page", throwable)
                        throwable.printStackTrace()

                        if (++loaded >= totalCells) {
                            binding.homeCellsRv.adapter = HomeAdapter(ArrayList(homepageCells))
                            loadingPanel?.visibility = View.GONE
                            binding.internetConnectionHome.visibility = View.GONE
                            binding.homeRefreshLayout.isRefreshing = false
                        }
                    }
                })
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        this.setTitle(getString(R.string.home))
        mActivity.toolbar.visibility = View.GONE
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            binding.initials.text = initials
        } else {
            binding.profileBackground.setImageDrawable(
                resources.getDrawable
                (R.drawable.ic_guest_avatar, context?.theme))
        }
        mActivity.setSelectedTab(MainActivity.HOME)
        mActivity.showBottomBar()
    }

    private fun setTitle(title: CharSequence) {
        binding.titleView.text = title
    }

    private fun initAppBar(view: View) {
        val firstName = sharedPreferences.getString(getString(R.string.first_name), null)
        firstName?.let {
            view.date_view.text = "Welcome, $it!".toUpperCase(Locale.getDefault())
            Handler().postDelayed(
                {
                    view.date_view.text = Utils.getCurrentSystemTime()
                },
                4000
            )
        } ?: run {
            view.date_view.text = Utils.getCurrentSystemTime()
        }
        (view.appbar_home.layoutParams
            as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        view.profile.setOnClickListener {
            //TODO: Account Settings
        }
    }

    /**
     * Show a SnackBar message right below the app bar
     */
    @Suppress("DEPRECATION")
    private fun displaySnack(view: View, text: String) {
        (view as ViewGroup).showSneakerToast(message = text, doOnRetry = { }, sneakerColor = R.color.sneakerBlurColorOverlay)
    }

    enum class Cells {
        POLLS, NEWS, DINING, CALENDAR, LAUNDRY, POSTS
    }




}