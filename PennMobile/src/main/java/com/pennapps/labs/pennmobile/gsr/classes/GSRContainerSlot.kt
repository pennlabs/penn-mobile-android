package com.pennapps.labs.pennmobile.gsr.classes

import org.joda.time.DateTime

/**
 * Created by Varun on 10/14/2018.
 */

class GSRContainerSlot(
    val timeRange: String,
    val startTime: DateTime,
    val elementId: String,
    val gid: Int,
    val roomId: Int,
    val roomName: String,
    val start: String,
    val end: String,
)
