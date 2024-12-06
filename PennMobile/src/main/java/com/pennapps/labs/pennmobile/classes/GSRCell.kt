package com.pennapps.labs.pennmobile.classes

data class GSRCell(
    val reservations: List<GSRReservation>,
) : HomeCell() {
    init {
        type = "gsr_booking"
    }
}
