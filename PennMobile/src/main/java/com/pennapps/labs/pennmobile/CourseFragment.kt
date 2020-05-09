package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.Building
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.MapCallbacks
import com.pennapps.labs.pennmobile.classes.Review

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1

class CourseFragment : Fragment(), OnMapReadyCallback {

    @BindView(R.id.course_activity)
    internal var courseActivityTextView: TextView? = null
    @BindView(R.id.course_title)
    internal var courseTitleTextView: TextView? = null
    @BindView(R.id.instructor)
    internal var instructorTextView: TextView? = null
    @BindView(R.id.course_desc_title)
    internal var descriptionTitle: TextView? = null
    @BindView(R.id.course_desc)
    internal var descriptionTextView: TextView? = null
    @BindView(R.id.registrar_map_frame)
    internal var mapFrame: View? = null
    @BindView(R.id.pcr_layout)
    internal var pcrLayout: LinearLayout? = null
    @BindView(R.id.course_avg_course)
    internal var courseQuality: TextView? = null
    @BindView(R.id.course_avg_instr)
    internal var instructorQuality: TextView? = null
    @BindView(R.id.course_avg_diff)
    internal var courseDifficulty: TextView? = null
    private var unbinder: Unbinder? = null

    private var map: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var course: Course? = null
    private lateinit var mLabs: Labs
    private lateinit var mActivity: MainActivity
    private var fav: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = arguments?.getParcelable(getString(R.string.course_bundle_arg))
        mLabs = MainActivity.getLabsInstance()
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        fav = arguments?.getBoolean(getString(R.string.search_favorite), false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_course, container, false)
        unbinder = ButterKnife.bind(this, v)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val fm = childFragmentManager
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            fm.beginTransaction().add(R.id.registrar_map_container, mapFragment!!).commit()
            fm.executePendingTransactions()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.registrar_search)
        if (searchMenuItem != null) {
            val searchView = searchMenuItem.actionView as SearchView
            searchView.isEnabled = false
            searchMenuItem.isVisible = false
            searchView.clearFocus()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                var pos = SearchFavoriteFragment.getPagePosition()
                if (RegistrarTab.fragments[pos] == null) {
                    pos = (pos + 1) % 2
                }
                val fragmentManager = mActivity.supportFragmentManager
                fragmentManager.beginTransaction().remove(RegistrarTab.fragments[pos]).commit()
                RegistrarTab.fragments[pos] = null

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (containsNum(mActivity.title)) {
            val builder = StringBuilder(mActivity.title)
            val fav = arguments?.getBoolean(getString(R.string.registrar_search), false) ?: false
            if (fav) {
                builder.append(" - ").append(course?.name)
            } else {
                builder.insert(0, " - ").insert(0, course?.name)
            }
            mActivity.title = builder.toString()
        } else {
            mActivity.title = course?.name
        }
        if (map == null) {
            mapFragment?.getMapAsync(this)
        }
        processCourse()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(MapCallbacks.DEFAULT_LATLNG, 17f))
        map?.uiSettings?.isZoomControlsEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mActivity.title.toString().contains("-")) {
            val builder = StringBuilder(mActivity.title)
            val fav = arguments?.getBoolean(getString(R.string.registrar_search), false) ?: false
            if (fav) {
                builder.delete(builder.indexOf(" - "), builder.length)
            } else {
                builder.delete(0, builder.indexOf(" - ") + 2)
            }
            mActivity.title = builder.toString()
        } else {
            mActivity.setTitle(R.string.registrar)
        }
    }

    private fun drawCourseMap() {
        val buildingCode = course?.buildingCode
        val meetingLocation = course?.meetingLocation ?: ""
        if (buildingCode != "") {
            mLabs.buildings(buildingCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ buildings ->
                        if (!buildings.isEmpty()) {
                            drawMarker(buildings[0].latLng, meetingLocation)
                        }
                    }, { })
        }
    }

    private fun drawMarker(courseLatLng: LatLng?, meetingLocation: String) {
        val days = course?.meetingDays
        val times = course?.meetingStartTime
        val markerText = "$days $times $meetingLocation"
        if (map != null && courseLatLng != null && mapFrame != null) {
            mapFrame?.visibility = View.VISIBLE
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(courseLatLng, 17f))
            val marker = map?.addMarker(MarkerOptions()
                    .position(courseLatLng)
                    .title(markerText))
            marker?.showInfoWindow()
        }
    }

    private fun findCourseReviews() {
        mLabs.course_review(course!!.course_department + "-" + course?.course_number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ review ->
                    pcrLayout?.visibility = View.VISIBLE
                    courseQuality?.text = review.courseQuality()
                    instructorQuality?.text = review.instructorQuality()
                    courseDifficulty?.text = review.difficulty()
                }, { })
    }

    private fun processCourse() {
        val instructorsText: String

        drawCourseMap()

        val activityText = course?.activity ?: ""
        if (course?.instructors?.size ?: 0 > 0) {
            instructorsText = course!!.instructors[0].name
        } else {
            instructorsText = getString(R.string.professor_missing)
        }
        val courseTitleText = course?.course_title ?: ""
        val courseDescription = course?.course_description ?: ""

        courseActivityTextView?.text = activityText
        courseTitleTextView?.text = courseTitleText
        instructorTextView?.text = instructorsText
        if (instructorsText == getString(R.string.professor_missing)) {
            instructorTextView?.setTextColor(resources.getColor(R.color.color_primary_light))
        }

        if (courseDescription == "") {
            descriptionTitle?.visibility = View.GONE
            descriptionTextView?.visibility = View.GONE
        } else {
            descriptionTitle?.visibility = View.VISIBLE
            descriptionTextView?.visibility = View.VISIBLE
            descriptionTextView?.text = courseDescription
        }

        findCourseReviews()
    }

    companion object {

        fun containsNum(cs: CharSequence): Boolean {
            val s = cs.toString()
            for (c in s.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true
                }
            }
            return false
        }
    }
}