package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pennapps.labs.pennmobile.adapters.RegistrationsAdapter
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_manage_alerts.view.*

class PennCourseAlertManageAlertsFragment : Fragment() {

    private val viewModel: PennCourseAlertViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrationsAdapter
    private lateinit var deleteButton: ImageView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penn_course_alert_manage_alerts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.registrations_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = RegistrationsAdapter()
        recyclerView.adapter = adapter

        viewModel.userRegistrations.observe(viewLifecycleOwner, Observer {
                list ->
            run {
                Log.i("PCA", "list size is ${list.size}")
                adapter.submitList(null)
                adapter.submitList(list)
                swipeRefresh.isRefreshing = false
            }
        })

        viewModel.retrieveRegistrations()

        deleteButton = view.findViewById(R.id.deleteRegistrations)
        deleteButton.setOnClickListener {
            viewModel.deleteRegistrations()
        }

        swipeRefresh = view.findViewById(R.id.pca_manage_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.retrieveRegistrations()
        }

    }


}