package com.pennapps.labs.pennmobile.classes

data class LaundryCell(val roomId: Int) : HomeCell2() {
    init {
        type = "laundry" 
    }
}
