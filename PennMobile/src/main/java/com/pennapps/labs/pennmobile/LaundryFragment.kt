package com.pennapps.labs.pennmobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_laundry.*
import kotlinx.android.synthetic.main.fragment_laundry.view.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.loading_panel.view.*
import kotlinx.android.synthetic.main.no_results.*
import java.util.*

class LaundryFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    private lateinit var mStudentLife: StudentLife
    private lateinit var mContext: Context

    private var sp: SharedPreferences? = null

    // list of favorite laundry rooms
    private var laundryRooms = ArrayList<LaundryRoom>()
    // data for laundry room usage
    private var roomsData: List<LaundryUsage> = ArrayList()

    private var laundryRoomsResult = ArrayList<LaundryRoom>()
    private var roomsDataResult: MutableList<LaundryUsage> = ArrayList()

    private var mAdapter: LaundryRoomAdapter? = null

    private var count: Int = 0
    private var numRooms: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mContext = mActivity
        setHasOptionsMenu(true)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Laundry")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mContext).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_laundry, container, false)

        initAppBar(view)

        // get num rooms to display
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        numRooms = sp?.getInt(mContext.getString(R.string.num_rooms_pref), 100) ?: 0
        count = 0
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(i.toString(), false)) {
                count += 1
            }
        }

        view.favorite_laundry_list?.layoutManager = LinearLayoutManager(mContext)
        view.laundry_machine_refresh?.setOnRefreshListener { updateRooms() }
        view.laundry_machine_refresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        // no rooms chosen
        if (count == 0) {
            view.loadingPanel?.visibility = View.GONE
            view.laundry_help_text?.visibility = View.VISIBLE
            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
            view.favorite_laundry_list?.adapter = mAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.removeTabs()
        mActivity.toolbar.visibility = View.GONE
        numRooms = sp?.getInt(mContext.getString(R.string.num_rooms_pref), 100) ?: 0

        // get num rooms to display
        count = 0
        for (i in 0 until numRooms) {
            if (sp?.getBoolean(i.toString(), false) == true) {
                count += 1
            }
        }
        mActivity.setTitle(R.string.laundry)
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(MainActivity.LAUNDRY)
        }
        loadingPanel?.visibility = View.VISIBLE
        updateRooms()
    }

    private fun initAppBar(view: View) {
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.title_view.text = getString(R.string.laundry)
        view.date_view.text = Utils.getCurrentSystemTime()
        view.laundry_preferences.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LaundrySettingsFragment())
                    .addToBackStack("Laundry Settings Fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.laundry_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.laundry_settings) {
            val fragmentManager = mActivity.supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LaundrySettingsFragment())
                    .addToBackStack("Laundry Settings Fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateRooms() {

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionLaundry?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_laundry?.text = getString(R.string.internet_error)
            internetConnectionLaundry?.visibility = View.VISIBLE
        } else {
            internetConnectionLaundry?.visibility = View.GONE
        }

        laundryRooms = ArrayList()
        roomsData = ArrayList()
        roomsDataResult = ArrayList()
        laundryRoomsResult = ArrayList()


        // add data
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(i.toString(), false)) {
                addAvailability(i)
                addRoom(i)
            }
        }

        // no rooms chosen
        if (count == 0) {
            loadingPanel?.visibility = View.GONE
            laundry_help_text?.visibility = View.VISIBLE
            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
            favorite_laundry_list?.adapter = mAdapter
        }
    }

    @Synchronized
    private fun addRoomToList(room: LaundryRoom) {
        laundryRoomsResult.add(room)
    }

    @Synchronized
    private fun addUsageToList(usage: LaundryUsage) {
        roomsDataResult.add(usage)
    }

    private fun addRoom(i: Int) {
        mStudentLife.room(i)?.subscribe({ room ->
                    room.id = i
                    addRoomToList(room)

                    if (laundryRoomsResult.size == count) {

                        // sort laundry rooms data by hall name
                        roomsDataResult.sortWith(Comparator { usage1, usage2 -> usage2.id - usage1.id })

                        // sort laundry rooms by name
                        laundryRoomsResult.sortWith(Comparator { room1, room2 -> room2.id - room1.id })

                        var loading = false
                        // make sure results are finished loading
                        while (roomsDataResult.size != count) {
                            loading = true
                        }

                        // update UI
                        mActivity.runOnUiThread {
                            roomsData = roomsDataResult
                            laundryRooms = laundryRoomsResult
                            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
                            favorite_laundry_list?.adapter = mAdapter
                            no_results?.visibility = View.GONE
                            loadingPanel?.visibility = View.GONE
                            laundry_help_text?.visibility = View.INVISIBLE
                            laundry_machine_refresh?.isRefreshing = false

                        }
                    }
                }, {
                    mActivity.runOnUiThread {
                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.VISIBLE
                        laundry_help_text?.visibility = View.GONE
                        laundry_machine_refresh?.isRefreshing = false
                        Log.e("Laundry", "Error getting laundry data: " + it.stackTrace)
                    }
                })
    }

    private fun addAvailability(i: Int) {
        mStudentLife.usage(i)?.subscribe({ usage ->
                    usage.id = i
                    addUsageToList(usage)
                }, {
                    // in case usage data not available - set chart to 0
                    val newUsage = LaundryUsage()
                    newUsage.setWasherData()
                    newUsage.setDryerData()
                    newUsage.id = i
                    roomsDataResult.add(newUsage)

                    mActivity.runOnUiThread {
                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.VISIBLE
                        laundry_help_text?.visibility = View.GONE
                        laundry_machine_refresh?.isRefreshing = false
                    }
                })
    }

}