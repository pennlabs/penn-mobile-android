package com.pennapps.labs.pennmobile.laundry.classes

import com.pennapps.labs.pennmobile.home.classes.HomeCell

data class LaundryCell(
    val roomId: Int,
) : HomeCell() {
    init {
        type = "laundry"
    }
}
