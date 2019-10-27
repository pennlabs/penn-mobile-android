package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class HomeFragment : Fragment()  {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Home")
                .putContentType("App Feature")
                .putContentId("9"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.home_cells_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout.setOnRefreshListener { getHomePage() }

        getHomePage()

        return view
    }

    private fun getHomePage() {

        // get session id from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val sessionid = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")

        // get API data
        val labs = MainActivity.getLabsInstance() //TODO: get for an account id
        labs.getHomePage("test_android", "5fb78cbc-692e-4167-8802-82c3eb2ddc7b").subscribe({ cells ->
            mActivity.runOnUiThread {
                home_cells_rv.adapter = HomeAdapter(ArrayList(cells))
                loadingPanel.visibility = View.GONE
                try {
                    home_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load Home page", Toast.LENGTH_LONG).show()
                loadingPanel.visibility = View.GONE
                try {
                    home_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        })
    }
}