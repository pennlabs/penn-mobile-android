package com.pennapps.labs.pennmobile.classes

import org.joda.time.DateTime
import java.util.ArrayList

/**
 * Created by Varun on 10/14/2018.
 */

class GSRContainer(val gsrName: String, constructorTimeRange: String, constructorStartTime: DateTime,
                   constructorElementId: String, val gid: Int, val roomId: Int) {
    //used to keep track availability of the given room
    val availableGSRSlots = ArrayList<GSRContainerSlot>()

    init {

        val newGSRSlot = GSRContainerSlot(constructorTimeRange, constructorStartTime, constructorElementId, gid, roomId, gsrName)

        availableGSRSlots.add(newGSRSlot)

    }

    fun addGSRSlot(constructorTimeRange: String, constructorStartTime: DateTime,
                   constructorElementId: String) {

        //created new GSR time slot object
        val newGSRSlot = GSRContainerSlot(constructorTimeRange, constructorStartTime, constructorElementId, gid, roomId, gsrName)

        availableGSRSlots.add(newGSRSlot)
    }
}

