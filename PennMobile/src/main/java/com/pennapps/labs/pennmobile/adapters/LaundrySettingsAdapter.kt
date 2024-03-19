package com.pennapps.labs.pennmobile.adapters

import StudentLife
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.MainActivity.Companion.studentLifeInstance
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.classes.LaundryRequest
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Jackie on 2017-10-13.
 */
class LaundrySettingsAdapter(
    private val mContext: Context,
    private val laundryRooms: HashMap<String, List<LaundryRoomSimple>>,
    private val laundryHalls: List<String>
) : BaseExpandableListAdapter() {
    private val sp: SharedPreferences
    private val s: String
    private val switches: MutableList<Switch> = ArrayList()
    private val maxNumRooms = 3
    private var studentLife: StudentLife
    private var bearerToken: String

    init {
        sp = PreferenceManager.getDefaultSharedPreferences(mContext)
        s = mContext.getString(R.string.num_rooms_selected_pref)
        val mainActivity = mContext as MainActivity
        bearerToken = "Bearer " + sp.getString(mainActivity.getString(R.string.access_token), "")
        studentLife = studentLifeInstance

        // first time
        if (sp.getInt(s, -1) == -1) {
            val editor = sp.edit()
            editor.putInt(s, 0)
            editor.apply()
        }
    }

    override fun getGroupCount(): Int {
        return laundryHalls.size
    }

    override fun getChildrenCount(i: Int): Int {
        return laundryRooms[laundryHalls[i]]!!.size
    }

    override fun getGroup(i: Int): Any {
        return laundryHalls[i]
    }

    override fun getChild(i: Int, i1: Int): Any {
        return laundryRooms[laundryHalls[i]]!![i1]
    }

    override fun getGroupId(i: Int): Long {
        return i.toLong()
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return i1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    // view for the laundry buildings
    override fun getGroupView(i: Int, b: Boolean, view: View, viewGroup: ViewGroup): View {
        var view = view
        val laundryHallName = getGroup(i) as String
        if (view == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.laundry_settings_parent_item, null)
        }
        val textView = view.findViewById<TextView>(R.id.laundry_building_name)
        textView.text = laundryHallName
        val imageView = view.findViewById<ImageView>(R.id.laundry_building_dropdown)
        val buildingSwitch = view.findViewById<Switch>(R.id.laundry_building_favorite_switch)

        // if there is only one laundry room in the building, don't have dropdown
        if (laundryRooms[laundryHallName]!!.size == 1) {
            buildingSwitch.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            val laundryRoom = laundryRooms[laundryHallName]!![0]

            // set the Switch to the correct on or off
            buildingSwitch.isChecked = sp.getBoolean(Integer.toString(laundryRoom.id!!), false)

            // add the switch to the list - to aid with disabling
            if (!switches.contains(buildingSwitch)) {
                switches.add(buildingSwitch)
            }

            // max number of rooms
            if (sp.getInt(s, -1) >= maxNumRooms) {
                buildingSwitch.isEnabled = buildingSwitch.isChecked
            }
            buildingSwitch.setOnClickListener {
                val isChecked = buildingSwitch.isChecked
                val editor = sp.edit()
                val id = Integer.toString(laundryRoom.id!!)
                editor.putBoolean(id, isChecked)
                editor.apply()

                // update the numRoomSelected
                if (isChecked) {
                    editor.putString(
                        id + mContext.getString(R.string.location),
                        laundryRoom.location
                    )
                    editor.putInt(s, sp.getInt(s, -1) + 1)
                    editor.apply()
                } else {
                    editor.putInt(s, sp.getInt(s, -1) - 1)
                    editor.apply()
                }
                updateSwitches()
                sendPreferencesData()
            }
        } else {
            buildingSwitch.visibility = View.GONE
            imageView.setImageResource(R.drawable.ic_expand)
            imageView.visibility = View.VISIBLE
        }

        // if expanded
        if (b) {
            imageView.setImageResource(R.drawable.ic_collapse)
        }
        return view
    }

    override fun getChildView(i: Int, i1: Int, b: Boolean, view: View, viewGroup: ViewGroup): View {
        var view = view
        val laundryRoom = getChild(i, i1) as LaundryRoomSimple
        if (view == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.laundry_settings_child_item, null)
        }
        val textView = view.findViewById<TextView>(R.id.laundry_room_name)
        val name = laundryRoom.name
        textView.text = name
        val favoriteSwitch = view.findViewById<Switch>(R.id.laundry_favorite_switch)

        // set the Switch to the correct on or off
        favoriteSwitch.isChecked = sp.getBoolean(Integer.toString(laundryRoom.id!!), false)

        // add the switch to the list - to aid with disabling
        if (!switches.contains(favoriteSwitch)) {
            switches.add(favoriteSwitch)
        }

        // max number of rooms
        if (sp.getInt(s, -1) >= maxNumRooms) {
            favoriteSwitch.isEnabled = favoriteSwitch.isChecked
        }
        favoriteSwitch.setOnClickListener {
            val isChecked = favoriteSwitch.isChecked
            val editor = sp.edit()
            val id = Integer.toString(laundryRoom.id!!)
            editor.putBoolean(id, isChecked)
            editor.apply()

            // update the numRoomSelected
            if (isChecked) {
                editor.putString(id + mContext.getString(R.string.location), laundryRoom.location)
                editor.putInt(s, sp.getInt(s, -1) + 1)
                editor.apply()
            } else {
                editor.putInt(s, sp.getInt(s, -1) - 1)
                editor.apply()
            }
            updateSwitches()
            sendPreferencesData()
        }
        return view
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return false
    }

    private fun updateSwitches() {

        // maximum 3 rooms selected - disable all other switches
        if (sp.getInt(s, -1) >= maxNumRooms) {
            val iter: Iterator<Switch> = switches.iterator()
            while (iter.hasNext()) {
                val nextSwitch = iter.next()
                if (!nextSwitch.isChecked) {
                    nextSwitch.isEnabled = false
                }
            }
        } else {
            val iter: Iterator<Switch> = switches.iterator()
            while (iter.hasNext()) {
                val nextSwitch = iter.next()
                nextSwitch.isEnabled = true
            }
        }
    }

    private fun sendPreferencesData() {
        val favoriteLaundryRooms: MutableList<Int> = ArrayList()
        for (i in 0 until sp.getInt(mContext.getString(R.string.num_rooms_pref), 100)) {
            if (sp.getBoolean(Integer.toString(i), false)) {
                favoriteLaundryRooms.add(i)
            }
        }
        if (favoriteLaundryRooms.isEmpty()) {
            return
        }
        val mainActivity = mContext as MainActivity
        val oauth = OAuth2NetworkManager(mainActivity)
        oauth.getAccessToken {
            bearerToken =
                "Bearer " + sp.getString(mainActivity.getString(R.string.access_token), "")
            studentLife.sendLaundryPref(bearerToken, LaundryRequest(favoriteLaundryRooms))
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.i("Laundry", "Saved laundry preferences")
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Laundry", "Error saving laundry preferences: $t", t)
                    }

                })
        }
    }
}
