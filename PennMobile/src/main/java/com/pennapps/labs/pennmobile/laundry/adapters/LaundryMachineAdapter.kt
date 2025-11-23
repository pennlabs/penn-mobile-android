package com.pennapps.labs.pennmobile.laundry.adapters

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
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.airbnb.lottie.LottieAnimationView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.LaundryDryerItemBinding
import com.pennapps.labs.pennmobile.databinding.LaundryMachineItemBinding
import com.pennapps.labs.pennmobile.laundry.LaundryBroadcastReceiver
import com.pennapps.labs.pennmobile.laundry.classes.MachineDetail
import com.pennapps.labs.pennmobile.showSneakerToast

class LaundryMachineAdapter(
    var context: Context,
    var mMachineDetails: List<MachineDetail>,
    machineType: String,
    roomName: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRoomName: String
    private var mMachineType: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        if (mMachineType == "washer") {
            val itemBinding = LaundryMachineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LaundryMachineViewHolder(itemBinding, context, mMachineDetails)
        } else {
            val itemBinding = LaundryDryerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LaundryDryerViewHolder(itemBinding, context, mMachineDetails)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is LaundryMachineViewHolder -> holder.bind(position)
            is LaundryDryerViewHolder -> holder.bind(position)
        }
    }

    override fun getItemCount(): Int = mMachineDetails.size

    inner class LaundryMachineViewHolder(
        val itemBinding: LaundryMachineItemBinding,
        var context: Context,
        var machineDetails: List<MachineDetail>,
    ) : RecyclerView.ViewHolder(
            itemBinding.root,
        ) {
        var machineView: ImageView? = itemBinding.laundryMachineImageView
        var timeTextView: TextView? = itemBinding.minLeftTime
        var notificationBell: LottieAnimationView = itemBinding.bellNotificationIcon
        var alarmSwitch: SwitchCompat = itemBinding.laundryAlarmSwitch
        val animated = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_washer_in_use)

        fun bind(position: Int) {
            with(itemBinding.root) {
                val detail = mMachineDetails[position]
                if (alarmSwitch.visibility != View.GONE) {
                    alarmSwitch.visibility = View.GONE
                }

                when (val timeRemaining = detail.timeRemaining) {
                    NOT_AVAILABLE_LABEL -> {
                        if (mMachineType == context.getString(R.string.washer)) {
                            machineView!!.setImageResource(R.drawable.washer_na)
                        } else {
                            machineView!!.setImageResource(R.drawable.dryer_na)
                        }
                        timeTextView!!.setText(R.string.not_updating_status)
                        alarmSwitch.visibility = View.GONE
                    }

                    OPEN_LABEL -> {
                        if (mMachineType == context.getString(R.string.washer)) {
                            machineView!!.setImageResource(R.drawable.ic_washer_available)
                        } else {
                            machineView!!.setImageResource(R.drawable.ic_dryer_available)
                        }
                        timeTextView!!.setText(R.string.open)
                        alarmSwitch.visibility = View.GONE
                        val time = detail.timeRemaining
                        val id = detail.id
                        setSwitchState(time, id)
                    }

                    else -> {
                        if (mMachineType == context.getString(R.string.washer)) {
                            animated?.registerAnimationCallback(
                                object : Animatable2Compat.AnimationCallback() {
                                    override fun onAnimationEnd(drawable: Drawable?) {
                                        animated.start()
                                    }
                                },
                            )
                            machineView!!.setImageDrawable(animated)
                            animated?.start()
                        } else {
                            machineView!!.setImageResource(R.drawable.ic_dryer_in_use)
                        }
                        timeTextView!!.setTextColor(ContextCompat.getColor(context, R.color.secondary_text_color))
                        timeTextView!!.text = timeRemaining.toString()
                        val time = detail.timeRemaining
                        val id = detail.id
                        setSwitchState(time, id)
                        machineView?.setOnClickListener {
                            itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            alarmSwitch.performClick()
                        }
                    }
                }
            }
        }

        fun setSwitchState(
            time: Int,
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
                alarmSwitch.isChecked = true
                notificationBell.visibility = View.VISIBLE
                notificationBell.playAnimation()
            } else {
                alarmSwitch.isChecked = false
            }
            alarmSwitch.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (isChecked) {
                    notificationBell.visibility = View.VISIBLE
                    notificationBell.playAnimation()

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
                    notificationBell.visibility = View.INVISIBLE
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
    }

    inner class LaundryDryerViewHolder(
        val itemBinding: LaundryDryerItemBinding,
        var context: Context,
        var machineDetails: List<MachineDetail>,
    ) : RecyclerView.ViewHolder(
            itemBinding.root,
        ) {
        var machineView: ImageView? = itemBinding.laundryMachineImageView
        var timeTextView: TextView? = itemBinding.minLeftTime
        var notificationBell: LottieAnimationView = itemBinding.bellNotificationIcon
        var alarmSwitch: SwitchCompat = itemBinding.laundryAlarmSwitch

        fun bind(position: Int) {
            with(itemBinding.root) {
                val detail = mMachineDetails[position]
                alarmSwitch.visibility = View.GONE

                when (val timeRemaining = detail.timeRemaining) {
                    NOT_AVAILABLE_LABEL -> {
                        if (mMachineType == context.getString(R.string.washer)) {
                            machineView!!.setImageResource(R.drawable.washer_na)
                        } else {
                            machineView!!.setImageResource(R.drawable.dryer_na)
                        }
                        timeTextView!!.setText(R.string.not_updating_status)
                        alarmSwitch.visibility = View.GONE
                    }

                    OPEN_LABEL -> {
                        if (mMachineType == context.getString(R.string.washer)) {
                            machineView!!.setImageResource(R.drawable.ic_washer_available)
                        } else {
                            machineView!!.setImageResource(R.drawable.ic_dryer_available)
                        }
                        timeTextView!!.setText(R.string.open)
                        alarmSwitch.visibility = View.GONE
                        val time = detail.timeRemaining
                        val id = detail.id
                        setSwitchState(time, id)
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
                            machineView!!.setImageDrawable(animated)
                            animated?.start()
                        } else {
                            machineView!!.setImageResource(R.drawable.ic_dryer_in_use)
                        }
                        timeTextView!!.setTextColor(ContextCompat.getColor(context, R.color.secondary_text_color))
                        timeTextView!!.text = timeRemaining.toString()
                        val time = detail.timeRemaining
                        val id = detail.id
                        setSwitchState(time, id)
                        machineView?.setOnClickListener {
                            itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            alarmSwitch.performClick()
                        }
                    }
                }
            }
        }

        fun setSwitchState(
            time: Int,
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
                alarmSwitch.isChecked = true
                notificationBell.visibility = View.VISIBLE
                notificationBell.playAnimation()
            } else {
                alarmSwitch.isChecked = false
            }
            alarmSwitch.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (isChecked) {
                    notificationBell.visibility = View.VISIBLE
                    notificationBell.playAnimation()

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
                    notificationBell.visibility = View.INVISIBLE
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
    }

    // adds alarm to machine

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
