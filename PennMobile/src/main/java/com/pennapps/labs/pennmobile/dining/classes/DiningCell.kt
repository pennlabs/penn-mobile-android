package com.pennapps.labs.pennmobile.dining.classes

import com.pennapps.labs.pennmobile.home.classes.HomeCell

data class DiningCell(
    val venues: List<Int>,
) : HomeCell() {
    init {
        type = "dining"
    }
}
