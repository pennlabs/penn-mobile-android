package com.pennapps.labs.pennmobile.classes

import java.util.ArrayList

/**
 * Created by Varun on 10/14/2018.
 */

class GSRContainer(val gsrName: String, constructorTimeRange: String, constructorDateTime: String,
                   constructorDayDate: String, constructorDateNum: String, constructorDuration: String, constructorElementId: String) {
    //used to keep track availability of the given room
    val availableGSRSlots = ArrayList<GSRContainerSlot>()


    init {

        val newGSRSlot = GSRContainerSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId)

        availableGSRSlots.add(newGSRSlot)

    }

    fun addGSRSlot(constructorTimeRange: String, constructorDateTime: String,
                   constructorDayDate: String, constructorDateNum: String, constructorDuration: String, constructorElementId: String) {

        //created new GSR time slot object
        val newGSRSlot = GSRContainerSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId)

        availableGSRSlots.add(newGSRSlot)
    }
}

