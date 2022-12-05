package com.pennapps.labs.pennmobile

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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
            if (viewModel.userRegistrations.value?.isEmpty() == false) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Confirm Delete")
                builder.setMessage("Are you sure you want to delete all of your registrations?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteRegistrations()
                    dialog.cancel()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                val alert = builder.create()
                alert.show()
            } else {
                Toast.makeText(context, "You do not have any registrations yet!", Toast.LENGTH_SHORT).show()
            }
        }

        swipeRefresh = view.findViewById(R.id.pca_manage_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.retrieveRegistrations()
        }

    }


}