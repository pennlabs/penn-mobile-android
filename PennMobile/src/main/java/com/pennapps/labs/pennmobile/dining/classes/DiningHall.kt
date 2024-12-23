package com.pennapps.labs.pennmobile.dining.classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Interval

open class DiningHall : Parcelable {
    var id: Int
        private set
    var name: String?
        private set

    // Refers to whether the dining hall is residential or retail
    var isResidential: Boolean
        private set
    private var openHours: HashMap<String, Interval>
    var venue: Venue? = null
        private set
    var image: Int
        private set

    @SerializedName("tblDayPart")
    var menus: MutableList<Menu> = ArrayList()

    constructor(id: Int, name: String?, residential: Boolean, hours: HashMap<String, Interval>, venue: Venue?, image: Int) {
        this.id = id
        this.name = name
        isResidential = residential
        openHours = hours
        this.venue = venue
        this.image = image
    }

    protected constructor(`in`: Parcel) {
        val booleanArray = BooleanArray(1)
        `in`.readBooleanArray(booleanArray)
        isResidential = booleanArray[0]
        openHours = HashMap()
        menus = ArrayList()
        // Use application class loader instead of framework class loader because Menu is a custom class
        `in`.readMap(openHours as Map<*, *>, javaClass.classLoader)
        (menus as List<*>?)?.let { `in`.readList(it, javaClass.classLoader) }
        id = `in`.readInt()
        name = `in`.readString()
        image = `in`.readInt()
    }

    fun sortMeals(menus: MutableList<Menu>) {
        this.menus = menus
        val mealOrder = listOf("Breakfast", "Brunch", "Lunch", "Dinner", "Express")
        val comparator = Comparator { lhs: Menu, rhs: Menu -> mealOrder.indexOf(lhs.name) - mealOrder.indexOf(rhs.name) }
        this.menus.sortedWith(comparator)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeBooleanArray(booleanArrayOf(isResidential))
        dest.writeMap(openHours as Map<*, *>?)
        dest.writeList(menus as List<*>?)
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeInt(image)
    }

    // Returns list of time intervals sorted by interval starting time
    private fun orderedHours(): List<Interval?> {
        val list: List<Interval?> = ArrayList(openHours.values)
        val comparator = Comparator { x: Interval, y: Interval -> x.start.compareTo(y.start) }
        return list.sortedWith(comparator)
    }

    // Returns list of time intervals sorted by interval starting time, and merges intervals such that none overlap
    private fun orderedMergedHours(): List<Interval> {
        val originalList = orderedHours()
        val mergedList: MutableList<Interval> = ArrayList(originalList.size)
        var currentInterval: Interval? = null
        for (i in originalList.indices) {
            currentInterval =
                if (currentInterval == null) {
                    originalList[i]
                } else if (currentInterval.end >= originalList[i]!!.start) {
                    val newEndTime = if (currentInterval.end > originalList[i]!!.end) currentInterval.end else originalList[i]!!.end
                    Interval(currentInterval.start, newEndTime)
                } else {
                    mergedList.add(currentInterval)
                    null
                }
        }
        if (currentInterval != null) {
            mergedList.add(currentInterval)
        }
        return mergedList
    }

    // Takes the ordered time intervals of the dining hall and formats them for displaying to the user
    // e.g. 8 - 11 | 12 - 3 | 6 - 9
    fun openTimes(): String {
        // val list = if (isResidential) orderedHours() else orderedMergedHours()
        val list = orderedHours()
        val builder = StringBuilder()
        for (i in list.indices) {
            val openInterval = list[i]
            if (i != 0) {
                builder.append(" | ")
            }

            if (openInterval != null) {
                builder.append(getFormattedTime(openInterval.start))
                builder.append(" - ")
                builder.append(getFormattedTime(openInterval.end))
            }
        }
        return builder.toString()
    }

    private fun getFormattedTime(time: DateTime): String =
        if (time.toString("mm") == "00") {
            if (isResidential) {
                time.toString("h")
            } else {
                time.toString("h a")
            }
        } else {
            if (isResidential) {
                time.toString("h:mm")
            } else {
                time.toString("h:mm a")
            }
        }

    val isOpen: Boolean
        get() {
            for (openInterval in openHours.values) {
                if (openInterval.containsNow()) {
                    return true
                }
            }
            return false
        }

    // Returns the name of the meal that the dining hall is currently serving (e.g. Breakfast)
    fun openMeal(): String? {
        for ((key, openInterval) in openHours) {
            if (openInterval.containsNow()) {
                return key
            }
        }
        return null
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for a single menu, ie. Lunch, Dinner
     */
    open class Menu protected constructor(
        `in`: Parcel,
    ) : Parcelable {
        @SerializedName("service")
        var name: String = `in`.readString() ?: ""

        @SerializedName("stations")
        var stations: List<DiningStation> = ArrayList()

        @SerializedName("venue")
        var venue: DiningVenue? = null

        override fun describeContents(): Int = 0

        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeString(name)
        }

        companion object CREATOR : Parcelable.Creator<Menu?> {
            override fun createFromParcel(`in`: Parcel): Menu? = Menu(`in`)

            override fun newArray(size: Int): Array<Menu?> = arrayOfNulls(size)
        }
    }

    class DiningVenue {
        @SerializedName("venue_id")
        var venueId: Int = -1
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for a station at a dining hall
     */
    class DiningStation {
        @SerializedName("name")
        var name: String = ""

        @SerializedName("items")
        var items: List<FoodItem> = ArrayList()
    }

    /**
     * Created by Adel on 12/18/14.
     * Class for Food items in Dining menus
     */
    class FoodItem {
        @SerializedName("name")
        var title: String? = null

        @SerializedName("description")
        var description: String? = null
    }

    companion object CREATOR : Parcelable.Creator<DiningHall> {
        override fun createFromParcel(parcel: Parcel): DiningHall = DiningHall(parcel)

        override fun newArray(size: Int): Array<DiningHall?> = arrayOfNulls(size)
    }
}
