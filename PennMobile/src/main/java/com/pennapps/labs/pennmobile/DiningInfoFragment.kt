package com.pennapps.labs.pennmobile

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.VenueInterval
import kotlinx.android.synthetic.main.fragment_dining_info.view.*
import org.joda.time.format.DateTimeFormat
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Lily on 11/13/2015.
 * Fragment for Dining information (hours, map)
 */
class DiningInfoFragment : Fragment(), OnMapReadyCallback {

    private lateinit var menuParent: RelativeLayout
    private lateinit var mapFrame: View
    private var mDiningHall: DiningHall? = null
    private lateinit var mActivity: MainActivity
    private lateinit var mLabs: Labs
    private var map: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDiningHall = arguments?.getParcelable("DiningHall")
        mActivity = activity as MainActivity
        mLabs = MainActivity.labsInstance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_dining_info, container, false)
        v.setBackgroundColor(Color.WHITE)
        menuParent = v.dining_hours
        mapFrame = v.dining_map_frame
        fillInfo()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val fm = childFragmentManager
        mapFragment = SupportMapFragment.newInstance()
        fm.beginTransaction().add(R.id.dining_map_container, mapFragment).commit()
        fm.executePendingTransactions()
    }

    private fun drawMap() {
        val buildingCode = mDiningHall?.name
        if (buildingCode != "") {
            mLabs.buildings(buildingCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ buildings ->
                        if (buildings.isNotEmpty()) {
                            drawMarker(buildings[0].latLng)
                        }
                    }) { }
        }
    }

    private fun drawMarker(diningHallLatLng: LatLng?) {
        if (map != null && diningHallLatLng != null) {
            mapFrame.visibility = View.VISIBLE
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(diningHallLatLng, 17f))
            val marker = map?.addMarker(MarkerOptions()
                    .position(diningHallLatLng)
                    .title(mDiningHall?.name))
            marker?.showInfoWindow()
        }
    }

    fun fillInfo() {
        if (mDiningHall?.venue != null) {
            val days = mDiningHall?.venue?.allHours()  ?: ArrayList()
            var vertical = ArrayList<TextView>()
            for (day in days) {
                vertical = addDiningHour(day, vertical)
            }
        }
    }

    private fun addDiningHour(day: VenueInterval, vertical: ArrayList<TextView>): ArrayList<TextView> {
        val textView = TextView(mActivity)
        val intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val dateTime = intervalFormatter.parseDateTime(day.date)
        val dateString = dateTime.dayOfWeek().asText + ", " + dateTime.monthOfYear().asString + "/" + dateTime.dayOfMonth().asShortText
        textView.text = dateString
        textView.setTextAppearance(mActivity, R.style.DiningInfoDate)
        textView.setPadding(0, 40, 0, 0)

        if (vertical.isEmpty()) {
            textView.id = 0
            textView.id = textView.id + 10
            menuParent.addView(textView)
        } else {
            textView.id = vertical.last().id + 1
            val param = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            param.addRule(RelativeLayout.BELOW, vertical.last().id)
            param.setMargins(0, 10, 10, 0)
            menuParent.addView(textView, param)
        }
        vertical.add(textView)
        for (meal in day.meals) {
            val mealType = TextView(mActivity)
            mealType.text = meal.type
            mealType.id = vertical.last().id + 1
            val layparammeal = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            layparammeal.addRule(RelativeLayout.BELOW, vertical.last().id)
            layparammeal.setMargins(0, 10, 10, 0)
            menuParent.addView(mealType, layparammeal)
            vertical.add(mealType)
            val layparamtimes = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            layparamtimes.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, vertical.last().id)
            layparamtimes.addRule(RelativeLayout.ALIGN_BOTTOM, vertical.last().id)
            layparamtimes.setMargins(0, 10, 0, 0)
            val mealInt = TextView(mActivity)
            val hoursString = meal.getFormattedHour(meal.open) + " - " + meal.getFormattedHour(meal.close)
            mealInt.text = hoursString
            mealInt.id = vertical.last().id + 1
            menuParent.addView(mealInt, layparamtimes)
            vertical.add(mealInt)
        }
        return vertical
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = mActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onResume() {
        super.onResume()
        if (map == null) {
            mapFragment.getMapAsync(this)
        }
    }

    override fun onDestroyView() {
        setHasOptionsMenu(false)
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(39.95198, -75.19368), 17f))
        map?.uiSettings?.isZoomControlsEnabled = false
        if (isNetworkAvailable) {
            drawMap()
        } else {
            Toast.makeText(activity, resources.getString(R.string.no_data_msg), Toast.LENGTH_LONG).show()
        }
    }
}