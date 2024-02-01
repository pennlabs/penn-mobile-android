package com.pennapps.labs.pennmobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.adapters.LaundrySettingsAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.databinding.FragmentLaundrySettingsBinding
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*
import java.util.ArrayList
import java.util.HashMap

class LaundrySettingsFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    private lateinit var mContext: Context
    internal var mHelpLayout: RelativeLayout? = null

    private var sp: SharedPreferences? = null
    private var mButton: Button? = null

    private var numRooms: Int = 0

    private var _binding : FragmentLaundrySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mContext = mActivity
        mActivity.closeKeyboard()
        //setHasOptionsMenu(true)
        mActivity.toolbar.visibility = View.VISIBLE
        mActivity.hideBottomBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaundrySettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        // set up shared preferences
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)

        // reset laundry rooms button
        mButton = binding.laundryRoomReset
        mButton?.setOnClickListener {
            // remove shared preferences
            val editor = sp?.edit()
            editor?.putInt(getString(R.string.num_rooms_selected_pref), 0)
            editor?.apply()

            for (i in 0 until numRooms) {
                editor?.remove(i.toString())?.apply()
            }
        }

        // set up back button
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getHalls()
        return view
    }


    private fun getHalls() {
        mStudentLife.laundryRooms()
                .subscribe({ rooms ->
                    mActivity.runOnUiThread {
                        numRooms = rooms.size
                        // save number of rooms
                        val editor = sp?.edit()
                        editor?.putInt(getString(R.string.num_rooms_pref), numRooms)
                        editor?.apply()

                        val hashMap = HashMap<String, List<LaundryRoomSimple>>()
                        val hallList = ArrayList<String>()

                        var i = 0
                        // go through all the rooms
                        while (i < numRooms) {

                            // new list for the rooms in the hall
                            var roomList: MutableList<LaundryRoomSimple> = ArrayList()

                            // if hall name already exists, get the list of rooms and add to that
                            var hallName = rooms[i].location ?: ""

                            if (hallList.contains(hallName)) {
                                roomList = hashMap[hallName] as MutableList<LaundryRoomSimple>
                                hashMap.remove(hallName)
                                hallList.remove(hallName)
                            }

                            while (hallName == rooms[i].location) {
                                roomList.add(rooms[i])

                                i += 1
                                if (i >= rooms.size) {
                                    break
                                }
                            }

                            // name formatting for consistency
                            if (hallName == "Lauder College House") {
                                hallName = "Lauder"
                            }

                            // add the hall name to the list
                            hallList.add(hallName)
                            hashMap[hallName] = roomList
                        }

                        val mAdapter = LaundrySettingsAdapter(mContext, hashMap, hallList)
                        try {
                            binding.laundryBuildingExpandableList.setAdapter(mAdapter)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }

                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.GONE
                    }
                }, {
                    mActivity.runOnUiThread {
                        loadingPanel?.visibility = View.GONE
                        no_results?.visibility = View.VISIBLE
                        mHelpLayout?.visibility = View.GONE
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.laundry)
        mActivity.setSelectedTab(MainActivity.LAUNDRY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity.toolbar.visibility = View.GONE
        _binding = null
    }
}
