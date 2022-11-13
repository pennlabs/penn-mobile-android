package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.PennCourseAlertPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.view.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.view.date_view
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.view.title_view

class PennCourseAlertHolderFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var pagerAdapter: PennCourseAlertPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_penn_course_alert_holder, container, false)
        initAppBar(view)
        return view
    }

    private fun initAppBar(view: View?) {
//        val appbarHome = view?.findViewById<AppBarLayout>(R.id.pca_appbar_home) as CoordinatorLayout
//        appbarHome.behavior = ToolbarBehavior()
//        (view?.pca_appbar_home?.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        view?.title_view?.text = "Penn Course Alert"
        view?.date_view?.text = Utils.getCurrentSystemTime()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = PennCourseAlertPagerAdapter(this)
        viewPager = view.findViewById(R.id.PCAPager)
        viewPager.adapter = pagerAdapter
        tabLayout = view.findViewById(R.id.pca_tab_layout)
        TabLayoutMediator(tabLayout, PCAPager) { tab, position ->
            if (position == 0) {
                tab.text = "Create Alert"
            } else {
                tab.text = "Manage Alerts"
            }
        }.attach()
    }

//    private fun testGetAllRegistrations() {
//        var _response = ""
//        PennCourseAlertApi.retrofitService.getAllRegistrations().enqueue( object: Callback<List<PennCourseAlertRegistration>> {
//            override fun onFailure(call: Call<List<PennCourseAlertRegistration>>, t: Throwable) {
//                _response = "Failure: " + t.message
//                Log.i("PCA", "$_response")
//            }
//
//            override fun onResponse(call: Call<List<PennCourseAlertRegistration>>, response: Response<List<PennCourseAlertRegistration>>) {
////                val registrationList = response
//                Log.i("PCA", "${response.body()?.first()?.closeNotification}")
//            }
//        })
//    }

//    private fun testCreateRegistration() {
//        val registrationBody = PCARegistrationBody(1, "MATH-0030-101", false, false)
//        var _response = ""
//        PennCourseAlertApi.retrofitService.createRegistration(registrationBody).enqueue( object: Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                _response = "Failure: " + t.message
//                Log.i("PCA", _response)
//            }
//
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                Log.i("PCA", response.code().toString())
//                Log.i("PCA", response.body().toString())
//
//            }
//        })
//    }
//
//    private fun testGetRegistrationById() {
//        var _response = ""
//        PennCourseAlertApi.retrofitService.getRegistrationById("762236").enqueue( object: Callback<PennCourseAlertRegistration> {
//            override fun onFailure(call: Call<PennCourseAlertRegistration>, t: Throwable) {
//                _response = "Failure: " + t.message
//                Log.i("PCA", "$_response")
//            }
//
//            override fun onResponse(call: Call<PennCourseAlertRegistration>, response: Response<PennCourseAlertRegistration>) {
////                val registrationList = response
//                Log.i("PCA", "${response.body()?.id}")
//            }
//        })
//    }
//
//    private fun testUpdateRegistration() {
//        val registrationBody = PennCourseAlertUpdateBody(false, false, true, true, true)
//        var _response = ""
//        PennCourseAlertApi.retrofitService.updateRegistrationById("762236", registrationBody).enqueue( object: Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                _response = "Failure: " + t.message
//                Log.i("PCA", _response)
//            }
//
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                Log.i("PCA", "${response.code()}")
//            }
//        })
//    }
    //CIS-1050-001
}