package com.pennapps.labs.pennmobile

import android.app.AlertDialog
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.pennapps.labs.pennmobile.adapters.RegistrationsAdapter
import com.pennapps.labs.pennmobile.classes.PennCourseAlertRegistration
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_manage_alerts.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_manage_alerts.view.*
import kotlinx.android.synthetic.main.include_main.*
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
        return inflater.inflate(R.layout.fragment_penn_course_alert_manage_alerts, container, false)
    }

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


        viewModel.userRegistrations.observe(viewLifecycleOwner, Observer {
                list ->
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
            if (viewModel.userRegistrations.value.isNullOrEmpty()) {
                swipeRefresh.isRefreshing = false
            }
        }

        setNoDataViewsVisibility()

        //TODO: handle error toasts
//        viewModel.cancelRegistrationErrorToast.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(context, "Error canceling registration!", Toast.LENGTH_SHORT).show()
//            viewModel.onCancelToastDone()
//        })
//
//        viewModel.deleteRegistrationErrorToast.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(context, "Error deleting registration!", Toast.LENGTH_SHORT).show()
//            viewModel.onDeleteToastDone()
//        })

        //uncomment and add some viewmodel functionality to handle swipe item deletion
        /*
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedCourse: PennCourseAlertRegistration? =
//                    recyclerDataArrayList.get(viewHolder.adapterPosition)
                    viewModel.userRegistrations.value?.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
//                viewModel.userRegistrations.value?.removeAt(viewHolder.adapterPosition)
                viewModel.removeRegistration(viewHolder.adapterPosition)
                // below line is to notify our item is removed from adapter.
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // below line is to display our snackbar with action.
                deletedCourse?.section?.let {
                    Snackbar.make(recyclerView, it, Snackbar.LENGTH_LONG)
                        .setAction("Undo",
                            View.OnClickListener { // adding on click listener to our action of snack bar.
                                // below line is to add our item to array list with a position.
//                                viewModel.userRegistrations.value?.put .add(position, deletedCourse)

                                // below line is to notify item is
                                // added to our adapter class.
//                                recyclerViewAdapter.notifyItemInserted(position)
                            }).show()
                }
            } // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerView)
    */
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
        //TODO: implement to make switches work backend
//        Toast.makeText(context, "Item number $position clicked", Toast.LENGTH_SHORT).show()
//        when(adapter.currentList[position].closeNotification) {
//            true -> Toast.makeText(context, "Item number $position clicked and has close notifs ON", Toast.LENGTH_SHORT).show()
//            else -> Toast.makeText(context, "Item number $position clicked and has close notifs OFF", Toast.LENGTH_SHORT).show()
//        }
//        val id = adapter.currentList[position].id.toString()
//        viewModel.getRegistrationById(id)
    }

    override fun onClosedNotificationsSwitchClick(position: Int, onClosedNotificatons: Boolean) {
        val id = adapter.currentList[position].id.toString()
        Log.i("PCA_RV", "Item $position closedNoti" +
                " clicked with closed noti set to $onClosedNotificatons")
        if (subscribed_switch.isChecked) {
            viewModel.switchOnClosedNotifications(id, onClosedNotificatons)
        } else {
            notify_closed_switch.isChecked = false
            Toast.makeText(context, "Please toggle alert first to perform this action!",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSubscribedSwitchClick(position: Int, onSubscribeNotifications: Boolean) {
        Log.i("PCA_RV", "Item $position subscribed clicked with subscribed set to")
        val id = adapter.currentList[position].id.toString()
        if (onSubscribeNotifications) {
            viewModel.resubscribeToRegistration(id)
        } else {
//            notify_closed_switch.isClickable = false
            viewModel.cancelRegistration(id)
        }
    }


    private fun showInternetErrorBar(view: View) {
        val internetConnectionBanner = view.findViewById<Toolbar>(R.id.internetConnectionPCAManage)
        val internetConnectionMessage = view.findViewById<TextView>(R.id.internetConnection_message_pca_manage)
        internetConnectionBanner.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
        internetConnectionMessage.text = "Not Connected to Internet"
        internetConnectionBanner.visibility = View.VISIBLE
    }

    private fun hideInternetErrorBar(view: View) {
        val internetConnectionBanner = view.findViewById<Toolbar>(R.id.internetConnectionPCAManage)
        internetConnectionBanner.visibility = View.GONE
    }

}