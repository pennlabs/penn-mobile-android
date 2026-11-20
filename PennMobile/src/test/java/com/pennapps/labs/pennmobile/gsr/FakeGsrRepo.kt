package com.pennapps.labs.pennmobile.gsr

import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import kotlinx.coroutines.CompletableDeferred
import java.time.LocalDate

/** In-memory [GsrRepo] that records availability requests and can fail or pause on demand. */
class FakeGsrRepo(
    var locations: List<GsrLocation> = emptyList(),
    var rooms: List<GsrRoom> = emptyList(),
    var availabilityError: Exception? = null,
    var locationsError: Exception? = null,
    var savedLocationKey: String? = null,
    var userInfo: Triple<String, String, String> = Triple("", "", ""),
    var pennKey: String? = null,
    var bookingError: Exception? = null,
) : GsrRepo {
    val availabilityRequests = mutableListOf<Triple<GsrLocation, LocalDate, LocalDate>>()
    var locationRequests = 0
    val bookings = mutableListOf<Triple<String?, String?, Int>>()

    val bookingGates = ArrayDeque<CompletableDeferred<Unit>>()

    /** Each availability call waits on the next gate, if any, so tests can finish searches out of order. */
    val availabilityGates = ArrayDeque<CompletableDeferred<Unit>>()

    override suspend fun getAvailability(
        location: GsrLocation,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<GsrRoom> {
        availabilityRequests += Triple(location, startDate, endDate)
        availabilityGates.removeFirstOrNull()?.await()
        availabilityError?.let { throw it }
        return rooms
    }

    override suspend fun getLocations(): List<GsrLocation> {
        locationRequests++
        locationsError?.let { throw it }
        return locations
    }

    override fun lastLocationKey(): String? = savedLocationKey

    override fun saveLastLocation(location: GsrLocation) {
        savedLocationKey = location.key
    }

    override fun getSavedUserInfo() = userInfo

    override fun getSavedPennKey() = pennKey

    override suspend fun bookGsr(
        startTime: String?,
        endTime: String?,
        gid: Int,
        roomId: Int,
        roomName: String,
    ) {
        bookingGates.removeFirstOrNull()?.await()
        bookingError?.let { throw it }
        bookings += Triple(startTime, endTime, roomId)
    }

    override suspend fun cancelGsr(
        bookingId: String?,
        isHuntsmanReservation: Boolean,
    ) = Unit

    override suspend fun getReservations(): List<GSRReservation> = emptyList()
}
