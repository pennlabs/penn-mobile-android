package com.pennapps.labs.pennmobile.classes

interface HomepageDataModel {
    fun getSize() : Int
    fun getCell(position: Int) : HomeCell
}