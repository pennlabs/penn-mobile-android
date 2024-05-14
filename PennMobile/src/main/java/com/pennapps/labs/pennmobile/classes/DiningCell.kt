package com.pennapps.labs.pennmobile.classes

data class DiningCell(val venues: List<Int>) : HomeCell() {
    init {
        type = "dining"
    }
}
