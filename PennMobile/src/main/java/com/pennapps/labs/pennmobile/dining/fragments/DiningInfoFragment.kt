package com.pennapps.labs.pennmobile.dining.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.VenueInterval
import org.joda.time.format.DateTimeFormat

/**
 * Created by Lily on 11/13/2015.
 * Fragment for Dining information (hours)
 */
class DiningInfoFragment : Fragment() {
    private lateinit var menuParent: RelativeLayout
    private var mDiningHall: DiningHall? = null
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDiningHall = arguments?.getParcelable("DiningHall")
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.fragment_dining_info, container, false)
        v.setBackgroundColor(Color.WHITE)
        menuParent = v.findViewById(R.id.dining_hours)
        fillInfo()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun fillInfo() {
        if (mDiningHall?.venue != null) {
            val days = mDiningHall?.venue?.allHours() ?: ArrayList()
            var vertical = ArrayList<TextView>()
            for (day in days) {
                if (hasMeals(day)) {
                    vertical = addDiningHour(day, vertical)
                }
            }
        }
    }

    private fun hasMeals(day: VenueInterval): Boolean = day.meals.isNotEmpty()

    private fun addDiningHour(
        day: VenueInterval,
        vertical: ArrayList<TextView>,
    ): ArrayList<TextView> {
        val textView = TextView(mActivity)
        val intervalFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val dateTime = intervalFormatter.parseDateTime(day.date)
        val dateString = dateTime.dayOfWeek().asText + ", " + dateTime.monthOfYear().asShortText + " " + dateTime.dayOfMonth().asText

        textView.text = dateString
        textView.setTextAppearance(mActivity, R.style.DiningInfoDate)
        textView.setPadding(0, 40, 0, 0)
        textView.typeface = resources.getFont(R.font.gilroy_light)
        textView.setTextColor(resources.getColor(R.color.color_primary_dark))

        if (vertical.isEmpty()) {
            textView.id = 0
            textView.id = textView.id + 10
            menuParent.addView(textView)
        } else {
            textView.id = vertical.last().id + 1
            val param =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                )
            param.addRule(RelativeLayout.BELOW, vertical.last().id)
            param.setMargins(0, 10, 10, 0)
            menuParent.addView(textView, param)
        }
        vertical.add(textView)
        for (meal in day.meals) {
            val mealType = TextView(mActivity)
            mealType.text = meal.type
            mealType.id = vertical.last().id + 1
            val layparammeal =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                )
            layparammeal.addRule(RelativeLayout.BELOW, vertical.last().id)
            layparammeal.setMargins(0, 10, 10, 0)
            menuParent.addView(mealType, layparammeal)
            vertical.add(mealType)
            val layparamtimes =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                )
            layparamtimes.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, vertical.last().id)
            layparamtimes.addRule(RelativeLayout.ALIGN_BOTTOM, vertical.last().id)
            layparamtimes.setMargins(0, 10, 0, 0)
            val mealInt = TextView(mActivity)
            val hoursString = meal.open?.let { meal.getFormattedHour(it) } + " - " + meal.close?.let { meal.getFormattedHour(it) }
            mealInt.text = hoursString
            mealInt.id = vertical.last().id + 1
            menuParent.addView(mealInt, layparamtimes)
            vertical.add(mealInt)
        }
        return vertical
    }

    override fun onDestroyView() {
        setHasOptionsMenu(false)
        super.onDestroyView()
    }
}
