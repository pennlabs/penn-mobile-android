package com.pennapps.labs.pennmobile.gsr.classes

import com.pennapps.labs.pennmobile.home.classes.HomeCell

data class GSRCell(
    val reservations: List<GSRReservation>,
) : HomeCell() {
    init {
        type = "gsr_booking"
    }
}
