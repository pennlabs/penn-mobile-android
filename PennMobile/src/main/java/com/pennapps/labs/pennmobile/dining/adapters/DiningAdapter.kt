package com.pennapps.labs.pennmobile.dining.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.DiningListItemBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.fragments.MenuFragment
import com.squareup.picasso.Picasso
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.Collections

class DiningAdapter(
    private var diningHalls: List<DiningHall>,
) : RecyclerView.Adapter<DiningAdapter.DiningViewHolder>() {
    private lateinit var mStudentLife: StudentLife
    private lateinit var loaded: BooleanArray
    private lateinit var sortBy: String
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DiningViewHolder {
        val itemBinding = DiningListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mStudentLife = MainActivity.studentLifeInstance
        loaded = BooleanArray(diningHalls.size)
        context = parent.context
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sortBy = sp.getString("dining_sortBy", "RESIDENTIAL") ?: "RESIDENTIAL"
        Collections.sort(diningHalls, MenuComparator())
        return DiningViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: DiningViewHolder,
        position: Int,
    ) {
        if (position < diningHalls.size) {
            val diningHall = diningHalls[position]

            // Show dining hall name, photo, status, and hours on the screen
            holder.menuArrow.visibility = View.GONE
            holder.progressBar.visibility = View.VISIBLE

            holder.hallNameTV.text = diningHall.name
            holder.hallNameTV.isSelected = true
            Picasso
                .get()
                .load(diningHall.image)
                .fit()
                .centerCrop()
                .into(holder.hallImage)

            if (diningHall.isOpen) {
                holder.hallStatus.background = ContextCompat.getDrawable(context, R.drawable.label_green)
                if (diningHall.openMeal() != "all" && diningHall.openMeal() != null) {
                    holder.hallStatus.setText(getOpenStatusLabel(diningHall.openMeal() ?: ""))
                }
                holder.hallHours.text = diningHall.openTimes().lowercase()
            } else {
                holder.hallStatus.setText(R.string.dining_hall_closed)
                holder.hallStatus.background = ContextCompat.getDrawable(context, R.drawable.label_red)
                val openTimes = diningHall.openTimes()
                if (openTimes.isEmpty()) {
                    holder.hallHours.setText(R.string.dining_closed)
                } else {
                    holder.hallHours.text = diningHall.openTimes().lowercase()
                }
            }
            // Load the menu for each dining hall
            if (diningHall.isResidential && !loaded[position]) {
                holder.progressBar.visibility = View.VISIBLE
                try {
                    mStudentLife
                        .dailyMenu(diningHall.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ newDiningHall ->
                            newDiningHall?.menus?.let { menus ->
                                diningHall.sortMeals(menus)
                                holder.progressBar.visibility = View.INVISIBLE
                                holder.menuArrow.visibility = View.VISIBLE
                                loaded[position] = true
                            }
                        }, {
                            holder.progressBar.visibility = View.VISIBLE
                            holder.menuArrow.visibility = View.GONE
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                holder.progressBar.visibility = View.GONE
                holder.menuArrow.visibility = View.VISIBLE
            }
            holder.layout.setOnClickListener {
                val mainActivity = context as MainActivity
                val fragment = MenuFragment()

                val args = Bundle()
                args.putParcelable("DiningHall", diningHall)
                fragment.arguments = args

                val fragmentManager = mainActivity.supportFragmentManager
                fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment, "DINING_INFO_FRAGMENT")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }

    // Converts the String representation of a meal name to its corresponding resource ID
    private fun getOpenStatusLabel(openMeal: String): Int =
        when (openMeal) {
            "Breakfast" -> R.string.dining_hall_breakfast
            "Brunch" -> R.string.dining_hall_brunch
            "Lunch" -> R.string.dining_hall_lunch
            "Dinner" -> R.string.dining_hall_dinner
            "Late Night" -> R.string.dining_hall_late_night
            else -> R.string.dining_hall_open
        }

    override fun getItemCount(): Int = diningHalls.size

    private inner class MenuComparator : Comparator<DiningHall> {
        override fun compare(
            diningHall: DiningHall,
            diningHall2: DiningHall,
        ): Int {
            when (sortBy) {
                "OPEN" -> {
                    if (diningHall.isOpen && !diningHall2.isOpen) {
                        return -1
                    } else if (diningHall2.isOpen && !diningHall.isOpen) {
                        return 1
                    }
                    return if (diningHall.isResidential && !diningHall2.isResidential) {
                        -1
                    } else if (diningHall2.isResidential && !diningHall.isResidential) {
                        1
                    } else {
                        diningHall.name?.compareTo(diningHall2.name ?: "") ?: 0
                    }
                }
                "RESIDENTIAL" -> return if (diningHall.isResidential && !diningHall2.isResidential) {
                    -1
                } else if (diningHall2.isResidential && !diningHall.isResidential) {
                    1
                } else {
                    0
                }
                else -> return diningHall.name?.compareTo(diningHall2.name ?: "") ?: 0
            }
        }
    }

    class DiningViewHolder(
        itemBinding: DiningListItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val layout: ConstraintLayout = itemBinding.diningListItemLayout
        val hallNameTV: TextView = itemBinding.itemDiningName
        val hallStatus: TextView = itemBinding.itemDiningStatus
        val hallImage: ImageView = itemBinding.itemDiningImage
        val hallHours: TextView = itemBinding.itemDiningHours
        val menuArrow: ImageView = itemBinding.diningHallMenuIndicator
        val progressBar: ProgressBar = itemBinding.diningProgress
    }
}
