package com.pennapps.labs.pennmobile

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent.Builder
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.FlingRecyclerViewAdapter
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_fling.*


class FlingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(context, Crashlytics())
        setHasOptionsMenu(true)
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Spring Fling")
                .putContentType("App Feature")
                .putContentId("7"))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fling_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle presses on the action bar items
        return when (item?.itemId) {
            R.id.fling_raffle -> {
                val url = "https://docs.google.com/forms/d/e/1FAIpQLSexkehYfGgyAa7RagaCl8rze4KUKQSX9TbcvvA6iXp34TyHew/viewform"
                val builder = Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, Uri.parse(url))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fling, container, false)
        val labs = MainActivity.getLabsInstance()
        labs.flingEvents.subscribe({ flingEvents ->
            activity?.runOnUiThread {
                fling_fragment_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                fling_fragment_recyclerview.adapter = FlingRecyclerViewAdapter(context, flingEvents)
            }
        }, { activity?.runOnUiThread { Toast.makeText(activity, "Error: Could not retrieve Spring Fling schedule", Toast.LENGTH_LONG).show() } })
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity)?.removeTabs()
        activity?.setTitle(R.string.spring_fling)
        if (Build.VERSION.SDK_INT > 17){
            (activity as MainActivity).setSelectedTab(9)
        }
    }

    companion object {
        fun newInstance(): FlingFragment {
            return FlingFragment()
        }
    }
}// Required empty public constructor
