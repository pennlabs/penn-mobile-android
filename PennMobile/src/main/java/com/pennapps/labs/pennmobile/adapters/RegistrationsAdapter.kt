package com.pennapps.labs.pennmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.PennCourseAlertRegistration
import okhttp3.internal.format

class RegistrationsAdapter: ListAdapter<PennCourseAlertRegistration, RegistrationsAdapter.ViewHolder>(RegistrationDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pca_registration_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class ViewHolder(registrationView: View): RecyclerView.ViewHolder(registrationView) {
        var courseIdText: TextView
        var isOpenText: TextView
        var subscribedSwitch: SwitchCompat
        var notifyClosedSwitch: SwitchCompat
        var statusCircle: ImageView
        var lastNotified: TextView

        init {
            courseIdText = registrationView.findViewById(R.id.course_id_textview)
            isOpenText = registrationView.findViewById(R.id.is_open_textview)
            subscribedSwitch = registrationView.findViewById(R.id.subscribed_switch)
            notifyClosedSwitch = registrationView.findViewById(R.id.notify_closed_switch)
            statusCircle = registrationView.findViewById(R.id.statusCircle)
            lastNotified = registrationView.findViewById(R.id.lastNotifiedAt)
        }

        fun bindTo(registration: PennCourseAlertRegistration) {
            courseIdText.text = registration.section
            isOpenText.text = when(registration.sectionStatus) {
                "O" -> "Open"
                "C" -> "Closed"
                "X" -> "Cancelled"
                else -> "Unlisted"
            }
            subscribedSwitch.isChecked = !registration.cancelled
            notifyClosedSwitch.isChecked = (registration.closeNotification && !registration.cancelled)
            if (!registration.lastNotificationSentAt.isNullOrEmpty()) {
                lastNotified.text = formatDate(registration.lastNotificationSentAt)
            }

            if (registration.sectionStatus == "O") {
                statusCircle.setImageResource(R.drawable.dining_insights_circle)
            } else {
                statusCircle.setImageResource(R.drawable.pca_circle_red)
            }
        }

    }

    private fun formatDate(date: String): String {
        val formattedDate = "Last Notified "
        return formattedDate.plus(date.slice(5..6)).plus("/").plus(date.slice(8..9)).plus("/").plus(date.slice(0..3))
            .plus(" at ").plus(date.slice(11..15))
    }

    private class RegistrationDiffCallBack : DiffUtil.ItemCallback<PennCourseAlertRegistration>() {
        override fun areItemsTheSame(oldItem: PennCourseAlertRegistration, newItem: PennCourseAlertRegistration): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: PennCourseAlertRegistration, newItem: PennCourseAlertRegistration): Boolean =
            oldItem == newItem
    }


}