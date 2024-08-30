package com.pennapps.labs.pennmobile.adapters

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.airbnb.lottie.LottieAnimationView
import com.pennapps.labs.pennmobile.LaundryBroadcastReceiver
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.MachineDetail
import com.pennapps.labs.pennmobile.showSneakerToast
import kotlinx.android.synthetic.main.laundry_dryer_item.view.bell_notification_icon
import kotlinx.android.synthetic.main.laundry_dryer_item.view.laundry_machine_image_view
import kotlinx.android.synthetic.main.laundry_dryer_item.view.min_left_time

class LaundryMachineAdapter(
    var context: Context,
    var mMachineDetails: List<MachineDetail>,
    machineType: String,
    roomName: String,
) : RecyclerView.Adapter<LaundryMachineAdapter.CustomViewHolder>() {
    private var mRoomName: String
    private var mMachineType: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CustomViewHolder {
        val view: View =
            if (mMachineType == context.resources.getString(R.string.washer)) {
                LayoutInflater.from(parent.context).inflate(R.layout.laundry_machine_item, parent, false)
            } else {
                LayoutInflater.from(parent.context).inflate(R.layout.laundry_dryer_item, parent, false)
            }
        return CustomViewHolder(view, context, mMachineDetails)
    }

    override fun onBindViewHolder(
        holder: CustomViewHolder,
        position: Int,
    ) {
        val detail = mMachineDetails[position]
        holder.alarmSwitch.visibility = View.GONE

        when (val timeRemaining = detail.timeRemaining) {
            NOT_AVAILABLE_LABEL -> {
                if (mMachineType == context.getString(R.string.washer)) {
                    holder.machineView!!.setImageResource(R.drawable.washer_na)
                } else {
                    holder.machineView!!.setImageResource(R.drawable.dryer_na)
                }
                holder.timeTextView!!.setText(R.string.not_updating_status)
                holder.alarmSwitch.visibility = View.GONE
            }
            OPEN_LABEL -> {
                if (mMachineType == context.getString(R.string.washer)) {
                    holder.machineView!!.setImageResource(R.drawable.ic_washer_available)
                } else {
                    holder.machineView!!.setImageResource(R.drawable.ic_dryer_available)
                }
                holder.timeTextView!!.setText(R.string.open)
                holder.alarmSwitch.visibility = View.GONE
                val time = detail.timeRemaining
                val id = detail.id
                setSwitchState(time, holder, id)
            }
            else -> {
                if (mMachineType == context.getString(R.string.washer)) {
                    // holder.machineView!!.setImageResource(R.drawable.ic_washer_in_use)
                    val animated = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_washer_in_use)
                    animated?.registerAnimationCallback(
                        object : Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                animated.start()
                            }
                        },
                    )
                    holder.machineView!!.setImageDrawable(animated)
                    animated?.start()
                } else {
                    holder.machineView!!.setImageResource(R.drawable.ic_dryer_in_use)
                }
                holder.timeTextView!!.setTextColor(ContextCompat.getColor(context, R.color.secondary_text_color))
                holder.timeTextView!!.text = timeRemaining.toString()
                val time = detail.timeRemaining
                val id = detail.id
                setSwitchState(time, holder, id)
                holder.machineView?.setOnClickListener {
                    holder.itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    holder.alarmSwitch.performClick()
                }
            }
        }
    }

    override fun getItemCount(): Int = mMachineDetails.size

    inner class CustomViewHolder(
        view: View,
        var context: Context,
        var machineDetails: List<MachineDetail>,
    ) : RecyclerView.ViewHolder(
            view,
        ) {
        var machineView: ImageView? = view.laundry_machine_image_view
        var timeTextView: TextView? = view.min_left_time
        var notificationBell: LottieAnimationView = view.bell_notification_icon
        var alarmSwitch: Switch = view.findViewById<View>(R.id.laundry_alarm_switch) as Switch
    }

    // adds alarm to machine
    private fun setSwitchState(
        time: Int,
        holder: CustomViewHolder,
        machineId: Int,
    ) {
        val id = (mRoomName + mMachineType).hashCode() + machineId
        val intent = Intent(context, LaundryBroadcastReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(context.resources.getString(R.string.laundry_room_name), mRoomName)
        intent.putExtra(context.resources.getString(R.string.laundry_machine_type), mMachineType)
        intent.putExtra(context.resources.getString(R.string.laundry_machine_id), id)
        val alarmIntent: PendingIntent? =
            PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )

        // switch is off if no alarm
        if (alarmIntent != null) {
            holder.alarmSwitch.isChecked = true
            holder.notificationBell.visibility = View.VISIBLE
            holder.notificationBell.playAnimation()
        } else {
            holder.alarmSwitch.isChecked = false
        }
        holder.alarmSwitch.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (isChecked) {
                holder.notificationBell.visibility = View.VISIBLE
                holder.notificationBell.playAnimation()

                val alarmIntent1 =
                    PendingIntent.getBroadcast(
                        context,
                        id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                // for testing 10 second notification
                // alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, alarmIntent);
                alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time * 60000] = alarmIntent1

                val message = context.resources.getQuantityString(R.plurals.laundry_alarm_on, time, time)
                (
                    ((context as Activity).window?.decorView as ViewGroup).showSneakerToast(
                        message,
                        null,
                        R.color.sneakerBlurColorOverlay,
                    )
                )
            } else {
                holder.notificationBell.visibility = View.INVISIBLE
                // cancel alarm if exists
                val alarmIntent1 =
                    PendingIntent.getBroadcast(
                        context,
                        id,
                        intent,
                        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
                    )
                if (alarmIntent1 != null) {
                    alarmManager.cancel(alarmIntent1)
                    alarmIntent1.cancel()
                }
                if (buttonView.context == null) {
                    return@setOnCheckedChangeListener
                }

                val message = context.resources.getString(R.string.laundry_alarm_off)
                (
                    ((context as Activity).window?.decorView as ViewGroup).showSneakerToast(
                        message,
                        null,
                        R.color.sneakerBlurColorOverlay,
                    )
                )
            }
        }
    }

    companion object {
        // labels for ordering
        const val OPEN_LABEL = 400
        const val NOT_AVAILABLE_LABEL = 404
    }

    init {

        // sort time remaining so that in use goes first, then open, then not available
        for (detail in mMachineDetails) {
            val timeRemaining = detail.timeRemaining
            val status = detail.status
            if (timeRemaining == 0) {
                detail.timeRemaining = OPEN_LABEL
            }
            if (status == context.resources.getString(R.string.status_out_of_order) ||
                status == context.resources.getString(R.string.status_not_online)
            ) {
                detail.timeRemaining = NOT_AVAILABLE_LABEL
            }
        }
        mMachineDetails = mMachineDetails.sorted()
        mMachineType = machineType
        mRoomName = roomName
    }
}
