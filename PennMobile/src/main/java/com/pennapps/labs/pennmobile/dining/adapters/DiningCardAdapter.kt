package com.pennapps.labs.pennmobile.dining.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.DiningListItemBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.fragments.MenuFragment
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DiningCardAdapter(
    val mActivity: MainActivity,
    halls: ArrayList<DiningHall>,
) : RecyclerView.Adapter<DiningCardAdapter.ViewHolder>() {
    private var favoriteHalls: ArrayList<DiningHall> = halls

    private lateinit var itemImage: ImageView
    private lateinit var itemName: TextView
    private lateinit var itemStatus: TextView
    private lateinit var itemHours: TextView

    private lateinit var mContext: Context

//    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        mContext = parent.context
//        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance
        val itemBinding = DiningListItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        itemBinding.diningProgress.visibility = GONE
        return ViewHolder(itemBinding)
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

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val currentHall = favoriteHalls[position]
        Glide
            .with(mContext)
            .load(currentHall.image)
            .fitCenter()
            .centerCrop()
            .into(itemImage)
        itemName.text = currentHall.name
        if (currentHall.isOpen) {
            itemStatus.background = ContextCompat.getDrawable(itemStatus.context, R.drawable.label_green)
            if (currentHall.openMeal() != "all") {
                itemStatus.setText(getOpenStatusLabel(currentHall.openMeal()!!))
            }
            itemHours.text = currentHall.openTimes().lowercase()
        } else {
            itemStatus.setText(R.string.dining_hall_closed)
            itemStatus.background = ContextCompat.getDrawable(itemStatus.context, R.drawable.label_red)
            val openTimes = currentHall.openTimes()
            if (openTimes.isEmpty()) {
                itemHours.setText(R.string.dining_closed)
            } else {
                itemHours.text = currentHall.openTimes().lowercase()
            }
        }

        // Load the menu for each dining hall
        if (currentHall.isResidential) {
            try {
                mStudentLife
                    .dailyMenu(currentHall.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ newDiningHall ->
                        newDiningHall?.let {
                            currentHall.sortMeals(it.menus)
                        }
                    }, {
                        Log.e("DiningCard", "Error loading menus", it)
                        Toast.makeText(mContext, "Error loading menus", Toast.LENGTH_SHORT).show()
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.itemView.setOnClickListener {
            val fragment = MenuFragment()

            val args = Bundle()
            args.putParcelable("DiningHall", currentHall)
            fragment.arguments = args

            val fragmentManager = mActivity.supportFragmentManager
            fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment, "DINING_INFO_FRAGMENT")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    override fun getItemCount(): Int = favoriteHalls.size

    inner class ViewHolder(
        itemBinding: DiningListItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemImage = itemBinding.itemDiningImage
            itemName = itemBinding.itemDiningName
            itemStatus = itemBinding.itemDiningStatus
            itemHours = itemBinding.itemDiningHours
        }
    }
}
