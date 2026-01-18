package com.pennapps.labs.pennmobile.dining.classes

import android.util.Log
import com.pennapps.labs.pennmobile.MainActivity
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DiningHallUtils {
    private val RESIDENTIAL_HALLS =
        listOf(
            "Hill House",
            "Lauder College House",
            "English House",
            "McClelland Express",
        )

    /**
     * Adds menus to the dining halls.
     *
     * @param venues The list of dining halls to add menus to.
     *
     * TODO: Rewrite this due to bad practice (directly modifying the list could lead to potential
     *       unexpected bugs and also seems less intuitive)
     */
    fun getMenus(venues: MutableList<DiningHall>) {
        try {
            val idVenueMap = mutableMapOf<Int, DiningHall>()
            venues.forEach { idVenueMap[it.id] = it }
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatted = current.format(formatter)
            val studentLife = MainActivity.studentLifeInstance
            studentLife
                .getMenus(formatted)
                .subscribeOn(Schedulers.io())
                .subscribe({ menus ->
                    menus?.filterNotNull()?.forEach { menu ->
                        menu.venue?.let { venue ->
                            idVenueMap[venue.venueId]?.let { diningHall ->
                                val diningHallMenus = diningHall.menus
                                diningHallMenus.add(menu)
                                diningHall.sortMeals(diningHallMenus)
                            }
                        }
                    }
                }, { throwable ->
                    Log.e("DiningFragment", "Error getting Menus", throwable)
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * A sort comparator for dining halls based on the selected sort order.
     * We use this in diningHalls.sortedWith{ } to give us our desired sorted list of dining
     * halls.
     *
     * @param sortOrder The sort order to use for sorting the dining halls.
     * @param diningHall1 The first dining hall to compare.
     * @param diningHall2 The second dining hall to compare.
     */
    fun compareDiningHallsForSort(
        sortOrder: DiningHallSortOrder,
        diningHall1: DiningHall,
        diningHall2: DiningHall,
    ): Int =
        when (sortOrder) {
            DiningHallSortOrder.Residential -> {
                // Check if each hall's name is in your list
                val isResidential1 = RESIDENTIAL_HALLS.contains(diningHall1.name.orEmpty())
                val isResidential2 = RESIDENTIAL_HALLS.contains(diningHall2.name.orEmpty())

                // Compare their residential status.
                // This makes `true` (residential) come before `false` (non-residential).
                val residentialCompare = isResidential2.compareTo(isResidential1)

                // If they are different (one is residential, one isn't), use that result.
                if (residentialCompare != 0) {
                    residentialCompare
                } else {
                    // Otherwise (both are residential OR both are not), tie-break alphabetically.
                    diningHall1.name
                        .orEmpty()
                        .compareTo(diningHall2.name.orEmpty(), ignoreCase = true)
                }
            }

            DiningHallSortOrder.Open -> {
                // "true comes before false"
                val openCompare = diningHall2.isOpen.compareTo(diningHall1.isOpen)

                // Use name as a tie-breaker (just like your original code)
                if (openCompare != 0) {
                    openCompare
                } else {
                    diningHall1.name
                        .orEmpty()
                        .compareTo(diningHall2.name.orEmpty(), ignoreCase = true)
                }
            }

            DiningHallSortOrder.Name -> {
                // "alphabetic too"
                diningHall1.name
                    .orEmpty()
                    .compareTo(diningHall2.name.orEmpty(), ignoreCase = true)
            }
        }
}
