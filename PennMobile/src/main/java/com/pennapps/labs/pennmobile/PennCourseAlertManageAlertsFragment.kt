package com.pennapps.labs.pennmobile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pennapps.labs.pennmobile.adapters.RegistrationsAdapter
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.pca_registration_list_item.*


class PennCourseAlertManageAlertsFragment : Fragment(), RegistrationsAdapter.OnItemClickListener {

    private val viewModel: PennCourseAlertViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RegistrationsAdapter
    private lateinit var deleteButton: ImageView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var mActivity: MainActivity
    private lateinit var noAlertsImage: ImageView
    private lateinit var noAlertsMessage: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_penn_course_alert_manage_alerts, container,
            false
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isOnline(context)) {
            showInternetErrorBar(view)
            Toast.makeText(context, "Could not load alerts", Toast.LENGTH_SHORT).show()
        } else {
            hideInternetErrorBar(view)
        }

        mActivity = activity as MainActivity
        noAlertsImage = view.findViewById(R.id.no_alerts_image)
        noAlertsMessage = view.findViewById(R.id.no_alerts_message)

        recyclerView = view.findViewById(R.id.registrations_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = RegistrationsAdapter(this)
        recyclerView.adapter = adapter


        viewModel.userRegistrations.observe(viewLifecycleOwner) { list ->
            run {
                Log.i("PCA", "list size is ${list.size}")
                adapter.submitList(null)
                adapter.submitList(list)
                swipeRefresh.isRefreshing = false
                if (list.isEmpty()) {
                    noAlertsImage.visibility = View.VISIBLE
                    noAlertsMessage.visibility = View.VISIBLE
                } else {
                    noAlertsImage.visibility = View.GONE
                    noAlertsMessage.visibility = View.GONE
                }
            }
        }

        viewModel.retrieveRegistrations()

        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.pca_delete_scaleup)
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.pca_delete_scaledown)

        deleteButton = view.findViewById(R.id.deleteRegistrations)

        deleteButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> deleteButton.startAnimation(scaleUp)
                MotionEvent.ACTION_DOWN -> deleteButton.startAnimation(scaleDown)
            }

            v?.onTouchEvent(event) ?: true
        }

        deleteButton.setOnClickListener {
            if (viewModel.userRegistrations.value?.isEmpty() == false) {
                val dialogLayout = layoutInflater.inflate(R.layout.pca_dialog_delete_registrations, null)
                val builder = AlertDialog.Builder(activity, R.style.dialog_style)
                builder.setView(dialogLayout)
                val alert = builder.create()
                alert.window?.setBackgroundDrawableResource(R.drawable.pca_dialog_bg_window)
                alert.show()

                val confirmButton = alert.findViewById<Button>(R.id.pca_dialog_confirm_button)
                confirmButton.setOnClickListener {
                    viewModel.deleteRegistrations()
                    alert.cancel()
                }
                val cancelButton = alert.findViewById<Button>(R.id.pca_dialog_cancel_button)
                cancelButton.setOnClickListener {
                    alert.cancel()
                }
            } else {
                Toast.makeText(
                    context,
                    "You do not have any registrations yet!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        swipeRefresh = view.findViewById(R.id.pca_manage_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.retrieveRegistrations()
            if (viewModel.userRegistrations.value.isNullOrEmpty()) {
                swipeRefresh.isRefreshing = false
            }
        }

        setNoDataViewsVisibility()
    }


    private fun setNoDataViewsVisibility() {
        // Check if the recycler view data is empty
        if (viewModel.userRegistrations.value.isNullOrEmpty()) {
            // Show no alerts message
            noAlertsMessage.visibility = View.VISIBLE
            // Show no alerts image
            noAlertsImage.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(position: Int) {
        Log.i("PCA_RV", "Item $position clicked")
    }

    override fun onClosedNotificationsSwitchClick(position: Int, onClosedNotifications: Boolean) {
        val id = adapter.currentList[position].id.toString()
        Log.i(
            "PCA_RV", "Item $position closedNoti" +
                    " clicked with closed noti set to $onClosedNotifications"
        )
        if (subscribed_switch.isChecked) {
            viewModel.switchOnClosedNotifications(id, onClosedNotifications)
        } else {
            notify_closed_switch.isChecked = false
            Toast.makeText(
                context, "Please toggle alert first to perform this action!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSubscribedSwitchClick(position: Int, onSubscribeNotifications: Boolean) {
        Log.i("PCA_RV", "Item $position subscribed clicked with subscribed set to")
        val id = adapter.currentList[position].id.toString()
        if (onSubscribeNotifications) {
            viewModel.resubscribeToRegistration(id)
        } else {
            viewModel.cancelRegistration(id)
        }
    }


    private fun showInternetErrorBar(view: View) {
        val internetConnectionBanner = view.findViewById<Toolbar>(R.id.internetConnectionPCAManage)
        val internetConnectionMessage =
            view.findViewById<TextView>(R.id.internetConnection_message_pca_manage)
        internetConnectionBanner.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
        internetConnectionMessage.text = "Not Connected to Internet"
        internetConnectionBanner.visibility = View.VISIBLE
    }

    private fun hideInternetErrorBar(view: View) {
        val internetConnectionBanner = view.findViewById<Toolbar>(R.id.internetConnectionPCAManage)
        internetConnectionBanner.visibility = View.GONE
    }

}