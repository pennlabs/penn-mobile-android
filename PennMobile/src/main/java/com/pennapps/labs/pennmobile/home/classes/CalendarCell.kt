package com.pennapps.labs.pennmobile.home.classes

data class CalendarCell(
    val events: List<CalendarEvent>,
) : HomeCell() {
    init {
        type = "calendar"
    }
}
