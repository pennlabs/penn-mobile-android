package com.pennapps.labs.pennmobile

import android.app.Fragment
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.classes.HomeScreenCell
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.gsr_no_reservations
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.gsr_reservations_refresh_layout
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.gsr_reservations_rv
import kotlinx.android.synthetic.main.loading_panel.*

class MainFragment : Fragment()  {

    private lateinit var mActivity: MainActivity
    private var cells: List<HomeScreenCell>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("GsrReservations")
                .putContentType("App Feature")
                .putContentId("9"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gsr_reservations, container, false)

        view.gsr_reservations_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        // handle swipe to refresh
        view.gsr_reservations_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.gsr_reservations_refresh_layout.setOnRefreshListener { getHomePage() }

        // get api data
        getHomePage()

        return view
    }

    private fun getHomePage() {

        // get session id from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val sessionid = sp.getString(getString(R.string.huntsmanGSR_SessionID), "") //TODO: pass in huntsman session id maybe

        // get API data
        val labs = MainActivity.getLabsInstance()
        labs.getHomePage("test_android", null).subscribe({ cells ->
            mActivity.runOnUiThread {
                gsr_reservations_rv.adapter = GsrReservationsAdapter(ArrayList(reservations))
                loadingPanel.visibility = View.GONE
                if (cells.size > 0) {
                    gsr_no_reservations.visibility = View.GONE
                } else {
                    gsr_no_reservations.visibility = View.VISIBLE
                }
                // stop refreshing
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load Home page", Toast.LENGTH_LONG).show()
                loadingPanel.visibility = View.GONE
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        })
    }
}