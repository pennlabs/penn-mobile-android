package com.pennapps.labs.pennmobile.gsr.repo

import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import java.time.LocalDate

interface GsrRepo {
    fun getSavedUserInfo(): Triple<String, String, String>

    /** Locations the signed-in user may book; the backend already filters Wharton/SEAS access. */
    suspend fun getLocations(): List<GsrLocation>

    /** Rooms and slots for [startDate] through [endDate] (inclusive, campus-local days). */
    suspend fun getAvailability(
        location: GsrLocation,
        startDate: LocalDate,
        endDate: LocalDate = startDate,
    ): List<GsrRoom>

    suspend fun bookGsr(
        startTime: String?,
        endTime: String?,
        gid: Int,
        roomId: Int,
        roomName: String,
        firstName: String,
        lastName: String,
        email: String,
    )

    suspend fun cancelGsr(
        bookingId: String?,
        isHuntsmanReservation: Boolean,
    )

    suspend fun getReservations(): List<GSRReservation>
}
