package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.pennapps.labs.pennmobile.DiningHolderFragment
import com.pennapps.labs.pennmobile.GsrTabbedFragment
import com.pennapps.labs.pennmobile.HomeFragment
import com.pennapps.labs.pennmobile.LaundryFragment
import com.pennapps.labs.pennmobile.more_fragments.MoreFragment
import java.util.*

class MainPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle?) : FragmentStateAdapter(fragmentManager, lifecycle!!) {

    companion object {

        const val HOME_ID = 0
        const val DINING_ID = 1
        const val GSR_ID = 2
        const val LAUNDRY_ID = 3
        const val MORE_ID = 4
        const val COUNT = 5
        fun updateMainList() {
//            val gson = Gson()
//
//            // below line is to get to string present from our
//            // shared prefs if not present setting it as null.
//            val json = sharedPreferences.getString("courses", null)
//
//            // below line is to get the type of our array list.
//            val type: Type = object : TypeToken<ArrayList<CourseRVModal?>?>() {}.type
//
//            // in below line we are getting data from gson
//            // and saving it to our array list
//            courseList = gson.fromJson<Any>(json, type) as ArrayList<CourseRVModal>
        }
        //val mainList : List<Int> = updateMainList() // [ HOME, DINING, GSR, LAUNDRY, FITNESS]
        val mainList : List<Int> = listOf(DINING_ID, GSR_ID, LAUNDRY_ID)
    }
    override fun createFragment(position: Int): Fragment {
        if(position == 0) {
            return HomeFragment()
        }
        if(position == 4) {
            return MoreFragment()
        }
        return when(mainList[position - 1]) {
            HOME_ID-> HomeFragment()
            DINING_ID-> DiningHolderFragment()
            GSR_ID-> GsrTabbedFragment()
            LAUNDRY_ID-> LaundryFragment()
            MORE_ID-> MoreFragment()
            else -> HomeFragment()
        }
    }

    override fun getItemCount(): Int {
        return COUNT
    }

}
