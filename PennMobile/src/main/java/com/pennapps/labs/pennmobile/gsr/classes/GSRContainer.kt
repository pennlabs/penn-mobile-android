package com.pennapps.labs.pennmobile.gsr.classes

import org.joda.time.DateTime
import java.util.ArrayList

/**
 * Created by Varun on 10/14/2018.
 */

class GSRContainer(
    val gsrName: String,
    constructorTimeRange: String,
    constructorStartTime: DateTime,
    constructorElementId: String,
    val gid: Int,
    val roomId: Int,
    val start: String,
    val end: String,
) {
    // used to keep track availability of the given room
    val availableGSRSlots = ArrayList<GSRContainerSlot>()

    init {

        val newGSRSlot =
            GSRContainerSlot(
                constructorTimeRange,
                constructorStartTime,
                constructorElementId,
                gid,
                roomId,
                gsrName,
                start,
                end,
            )

        availableGSRSlots.add(newGSRSlot)
    }

    fun addGSRSlot(
        constructorTimeRange: String,
        constructorStartTime: DateTime,
        constructorElementId: String,
        start: String,
        end: String,
    ) {
        // created new GSR time slot object
        val newGSRSlot =
            GSRContainerSlot(
                constructorTimeRange,
                constructorStartTime,
                constructorElementId,
                gid,
                roomId,
                gsrName,
                start,
                end,
            )

        availableGSRSlots.add(newGSRSlot)
    }
}
