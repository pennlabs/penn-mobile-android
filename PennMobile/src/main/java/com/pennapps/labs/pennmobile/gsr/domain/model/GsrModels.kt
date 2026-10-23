package com.pennapps.labs.pennmobile.gsr.domain.model

import java.time.OffsetDateTime

/**
 * A bookable GSR building. [lid] alone isn't unique (VP Ground Floor and Weigle share 1086),
 * so a location is identified by [lid] and [gid] together.
 */
data class GsrLocation(
    val lid: String,
    val gid: Int,
    val name: String,
    val kind: Kind,
    val bookableDays: Int,
) {
    enum class Kind { WHARTON, LIBCAL, PENNGRP, UNKNOWN }
}

data class GsrRoom(
    val roomId: Int,
    val name: String,
    val gid: Int,
    val capacity: Int?,
    val thumbnailUrl: String?,
    val slots: List<GsrSlot>,
)

data class GsrSlot(
    val start: OffsetDateTime,
    val end: OffsetDateTime,
    val isAvailable: Boolean,
)

/** One room for one contiguous time window that can be booked as a single reservation. */
data class GsrBookingOption(
    val lid: String,
    val gid: Int,
    val roomId: Int,
    val roomName: String,
    val start: OffsetDateTime,
    val end: OffsetDateTime,
)
