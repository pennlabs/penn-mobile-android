package com.pennapps.labs.pennmobile.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_dining_item.view.*


class DiningCardAdapter(halls: ArrayList<DiningHall>) : RecyclerView.Adapter<DiningCardAdapter.ViewHolder>() {

    var favoriteHalls:ArrayList<DiningHall>  = halls
    private lateinit var itemImage: ImageView
    private lateinit var itemName: TextView
    private lateinit var itemStatus: TextView
    private lateinit var itemHours: TextView


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentHall = favoriteHalls[position]
        Picasso.get().load(currentHall.image).fit().centerCrop().into(itemImage)
        itemName.text = currentHall.name
        if (currentHall.isOpen) {
            itemStatus.background = ContextCompat.getDrawable(itemStatus.context, R.drawable.label_green)
            if (currentHall.openMeal() != "all") {
                itemStatus.setText(getOpenStatusLabel(currentHall.openMeal()!!))
            }
            itemHours.text = currentHall.openTimes().toLowerCase()
        } else {
            itemStatus.setText(R.string.dining_hall_closed)
            itemStatus.background = ContextCompat.getDrawable(itemStatus.context, R.drawable.label_red)
            val openTimes = currentHall.openTimes()
            if (openTimes.isEmpty()) {
                itemHours.setText(R.string.dining_closed)
            } else {
                itemHours.text = currentHall.openTimes().toLowerCase()
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteHalls.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_dining_item, parent, false)
        return ViewHolder(view)
    }

    // Converts the String representation of a meal name to its corresponding resource ID
    private fun getOpenStatusLabel(openMeal: String): Int {
        return when (openMeal) {
            "Breakfast" -> R.string.dining_hall_breakfast
            "Brunch" -> R.string.dining_hall_brunch
            "Lunch" -> R.string.dining_hall_lunch
            "Dinner" -> R.string.dining_hall_dinner
            "Late Night" -> R.string.dining_hall_late_night
            else -> R.string.dining_hall_open
        }
    }

    inner class ViewHolder
    (itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemImage = itemView.item_dining_image
            itemName = itemView.item_dining_name
            itemStatus = itemView.item_dining_status
            itemHours = itemView.item_dining_hours
        }

    }
}
