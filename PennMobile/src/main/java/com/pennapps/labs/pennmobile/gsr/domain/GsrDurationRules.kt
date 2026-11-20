package com.pennapps.labs.pennmobile.gsr.domain

import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation

const val GSR_SLOT_MINUTES = 30

private const val BIOTECH_COMMONS_LID = "2683"
private const val BIOTECH_COMMONS_GID = 1885

/**
 * Longest single booking the app offers at this location.
 *
 * Keyed on kind and IDs rather than display names, which drift ("Biotech Commons" was once
 * matched as "Biomedical"). Wharton allows 90 minutes at a time; LibCal and SEAS allow 2 hours
 * a day. Biotech Commons keeps the 3 hours the app has always offered there.
 */
fun GsrLocation.maxDurationMinutes(): Int =
    when {
        kind == GsrLocation.Kind.WHARTON -> 90
        lid == BIOTECH_COMMONS_LID && gid == BIOTECH_COMMONS_GID -> 180
        else -> 120
    }

fun GsrLocation.durationOptions(): List<Int> = (GSR_SLOT_MINUTES..maxDurationMinutes() step GSR_SLOT_MINUTES).toList()
