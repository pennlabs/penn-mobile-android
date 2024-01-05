package com.pennapps.labs.pennmobile.classes

data class CalendarCell(val events: List<CalendarEvent>) : HomeCell2() {
    init {
        type = "calendar" 
    }
}

