package com.pennapps.labs.pennmobile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.FlingRecyclerViewAdapter

import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_fling.*
import android.R.id



class FlingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Spring Fling")
                .putContentType("App Feature")
                .putContentId("7"))
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
        activity?.setTitle(R.string.spring_fling)
        (activity as MainActivity?)?.setNav(R.id.nav_fling)
    }

    companion object {
        fun newInstance(): FlingFragment {
            return FlingFragment()
        }
    }
}// Required empty public constructor
