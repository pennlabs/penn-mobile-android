package com.pennapps.labs.pennmobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.LaundryRoomAdapter
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryUsage

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

import butterknife.BindView
import butterknife.ButterKnife
import io.fabric.sdk.android.Fabric
import rx.functions.Action1

class LaundryActivity : AppCompatActivity() {

    // views
    @BindView(R.id.laundry_help_text)
    internal var mTextView: TextView? = null
    @BindView(R.id.loadingPanel)
    internal var loadingPanel: RelativeLayout? = null
    @BindView(R.id.no_results)
    internal var no_results: TextView? = null
    @BindView(R.id.favorite_laundry_list)
    internal var mRecyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var mLabs: Labs? = null
    private var mContext: Context? = null

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
        setContentView(R.layout.activity_laundry)

        // back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mLabs = MainActivity.getLabsInstance()
        mContext = this

        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        numRooms = sp!!.getInt(mContext!!.getString(R.string.num_rooms_pref), 100)

        // get num rooms to display
        count = 0
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(Integer.toString(i), false)) {
                count += 1
            }
        }
        Fabric.with(this, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Laundry")
                .putContentType("App Feature")
                .putContentId("3"))

        ButterKnife.bind(this)

        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
        swipeRefreshLayout = findViewById<View>(R.id.laundry_machine_refresh) as SwipeRefreshLayout
        swipeRefreshLayout!!.setOnRefreshListener { updateRooms() }
        swipeRefreshLayout!!.setColorSchemeResources(R.color.color_accent, R.color.color_primary)

        // no rooms chosen
        if (count == 0) {
            loadingPanel!!.visibility = View.GONE
            mTextView!!.visibility = View.VISIBLE
            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
            mRecyclerView!!.adapter = mAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.laundry_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.laundry_settings) {
            val intent = Intent(this, com.pennapps.labs.pennmobile.LaundrySettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            return true
        } else if (id == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onResume() {
        super.onResume()

        numRooms = sp!!.getInt(mContext!!.getString(R.string.num_rooms_pref), 100)

        // get num rooms to display
        count = 0
        for (i in 0 until numRooms) {
            if (sp!!.getBoolean(Integer.toString(i), false)) {
                count += 1
            }
        }

        loadingPanel!!.visibility = View.VISIBLE
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
            loadingPanel!!.visibility = View.GONE
            mTextView!!.visibility = View.VISIBLE
            mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
            mRecyclerView!!.adapter = mAdapter
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
        mLabs!!.room(i)
                .subscribe({ room ->
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
                        runOnUiThread {
                            if (loadingPanel != null) {

                                roomsData = roomsDataResult
                                laundryRooms = laundryRoomsResult
                                mAdapter = LaundryRoomAdapter(mContext, laundryRooms, roomsData, false)
                                mRecyclerView!!.adapter = mAdapter

                                loadingPanel!!.visibility = View.GONE
                                mTextView!!.visibility = View.INVISIBLE
                            }
                            try {
                                swipeRefreshLayout!!.isRefreshing = false
                            } catch (e: NullPointerException) {
                                //it has gone to another page.
                            }
                        }
                    }
                }, {
                    runOnUiThread {
                        if (loadingPanel != null) {
                            loadingPanel!!.visibility = View.GONE
                        }
                        if (no_results != null) {
                            no_results!!.visibility = View.VISIBLE
                        }
                        if (mTextView != null) {
                            mTextView!!.visibility = View.GONE
                        }
                        try {
                            swipeRefreshLayout!!.isRefreshing = false
                        } catch (e: NullPointerException) {
                            //it has gone to another page.
                        }
                    }
                })
    }

    private fun addAvailability(i: Int) {
        mLabs!!.usage(i)
                .subscribe({ usage ->
                    usage.id = i
                    addUsageToList(usage)
                }, {
                    // in case usage data not available - set chart to 0
                    val newUsage = LaundryUsage()
                    newUsage.setWasherData()
                    newUsage.setDryerData()
                    newUsage.id = i
                    roomsDataResult.add(newUsage)

                    runOnUiThread {
                        if (loadingPanel != null) {
                            loadingPanel!!.visibility = View.GONE
                        }
                        if (no_results != null) {
                            no_results!!.visibility = View.VISIBLE
                        }
                        if (mTextView != null) {
                            mTextView!!.visibility = View.GONE
                        }
                        try {
                            swipeRefreshLayout!!.isRefreshing = false
                        } catch (e: NullPointerException) {
                            //it has gone to another page.
                        }
                    }
                })
    }
}