package com.pennapps.labs.pennmobile.classes

interface HomepageDataModel {
    fun getSize(): Int

    fun getCell(position: Int): HomeCell

    fun notifyPostBlurLoaded()

    fun notifyNewsBlurLoaded()

    fun updateDining(venues: List<Int>)

    fun getDiningHallPrefs(): List<Int>
}
