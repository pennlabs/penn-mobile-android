package com.pennapps.labs.pennmobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_laundry.*
import kotlinx.android.synthetic.main.fragment_laundry.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.loading_panel.view.*
import kotlinx.android.synthetic.main.no_results.*
import java.util.*

class LaundryFragment : Fragment() {


    private lateinit var mActivity: MainActivity

    private lateinit var mLabs: Labs
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

        mLabs = MainActivity.getLabsInstance()
        mActivity = activity as MainActivity
        mContext = mActivity
        setHasOptionsMenu(true)

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Laundry")
                .putContentType("App Feature")
                .putContentId("3"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_laundry, container, false)

        // get num rooms to display
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        numRooms = sp?.getInt(mContext?.getString(R.string.num_rooms_pref), 100) ?: 0
        count = 0
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(Integer.toString(i), false)) {
                count += 1
            }
        }

        view.favorite_laundry_list.layoutManager = LinearLayoutManager(mContext)
        view.laundry_machine_refresh.setOnRefreshListener { updateRooms() }
        view.laundry_machine_refresh.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        // no rooms chosen
        if (count == 0) {
            view.loadingPanel?.visibility = View.GONE
            view.laundry_help_text?.visibility = View.VISIBLE
            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
            view.favorite_laundry_list?.adapter = mAdapter
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.laundry_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.laundry_settings) {
            val intent = Intent(mContext, LaundrySettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()

        numRooms = sp?.getInt(mContext?.getString(R.string.num_rooms_pref), 100) ?: 0

        // get num rooms to display
        count = 0
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(Integer.toString(i), false)) {
                count += 1
            }
        }
        mActivity.setTitle(R.string.laundry)
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(3)
        }
        loadingPanel?.visibility = View.VISIBLE
        updateRooms()
    }

    private fun updateRooms() {

        laundryRooms = ArrayList()
        roomsData = ArrayList()
        roomsDataResult = ArrayList()
        laundryRoomsResult = ArrayList()

        // add data
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(Integer.toString(i), false)) {
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
        mLabs?.room(i)?.subscribe({ room ->
                    room.id = i
                    addRoomToList(room)

                    if (laundryRoomsResult.size == count) {

                        // sort laundry rooms data by hall name
                        Collections.sort(roomsDataResult) { usage1, usage2 -> usage2.id - usage1.id }

                        // sort laundry rooms by name
                        Collections.sort(laundryRoomsResult) { room1, room2 -> room2.id - room1.id }

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
                    }
                })
    }

    private fun addAvailability(i: Int) {
        mLabs?.usage(i)?.subscribe({ usage ->
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