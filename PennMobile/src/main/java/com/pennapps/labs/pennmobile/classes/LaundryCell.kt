package com.pennapps.labs.pennmobile.classes

data class LaundryCell(val roomId: Int) : HomeCell() {
    init {
        type = "laundry" 
    }
}
