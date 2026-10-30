package com.pennapps.labs.pennmobile.gsr.domain

import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import kotlinx.coroutines.CancellationException
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

val PENN_ZONE: ZoneId = ZoneId.of("America/New_York")

/**
 * Finds every room that is free for the whole window starting at [from], earliest first.
 *
 * Shared by the GSR screen and the AppFunctions entry point so both apply the same
 * contiguous-slot rules and report failures as the same [GsrSearchResult] cases.
 */
class FindAvailableGsrs
    @Inject
    constructor(
        private val repo: GsrRepo,
    ) {
        suspend operator fun invoke(
            location: GsrLocation,
            from: ZonedDateTime,
            durationMinutes: Int,
        ): GsrSearchResult {
            require(durationMinutes >= GSR_SLOT_MINUTES && durationMinutes % GSR_SLOT_MINUTES == 0) {
                "Duration must be a positive multiple of $GSR_SLOT_MINUTES minutes, was $durationMinutes"
            }
            val maxMinutes = location.maxDurationMinutes()
            if (durationMinutes > maxMinutes) {
                return GsrSearchResult.DurationTooLong(durationMinutes, maxMinutes, location)
            }

            val start = from.withZoneSameInstant(PENN_ZONE)
            // A window ending exactly at midnight doesn't need the next day's availability.
            // How: It pushes it back to 11:59:59 of the same day, hence we only get same day availability
            val lastDay = start.plusMinutes(durationMinutes.toLong()).minusSeconds(1).toLocalDate()

            return try {
                val rooms = repo.getAvailability(location, start.toLocalDate(), lastDay)
                val options = findBookableOptions(rooms, location, start.toOffsetDateTime(), durationMinutes)
                if (options.isEmpty()) GsrSearchResult.NoneAvailable else GsrSearchResult.Found(options)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toGsrFailure()
            }
        }
    }

/**
 * Builds every [durationMinutes]-long window made of back-to-back available slots.
 *
 * A slot already in progress still counts if it ends after [from], matching the original
 * GSR screen. Slots must touch end-to-start; slot length isn't assumed to be 30 minutes.
 */
internal fun findBookableOptions(
    rooms: List<GsrRoom>,
    location: GsrLocation,
    from: OffsetDateTime,
    durationMinutes: Int,
): List<GsrBookingOption> =
    rooms
        .flatMap { room ->
            val slots =
                room.slots
                    .filter { it.isAvailable && it.end.isAfter(from) }
                    .sortedBy { it.start.toInstant() }

            slots.indices.mapNotNull { first ->
                val windowEnd = slots[first].start.plusMinutes(durationMinutes.toLong())
                var last = first
                while (slots[last].end.isBefore(windowEnd)) {
                    val next = last + 1
                    if (next >= slots.size || !slots[next].start.isEqual(slots[last].end)) {
                        return@mapNotNull null
                    }
                    last = next
                }
                if (!slots[last].end.isEqual(windowEnd)) return@mapNotNull null

                GsrBookingOption(
                    lid = location.lid,
                    gid = location.gid,
                    roomId = room.roomId,
                    roomName = room.name,
                    start = slots[first].start,
                    // The backend's own end string must be sent back when booking.
                    end = slots[last].end,
                )
            }
        }.sortedWith(compareBy({ it.start.toInstant() }, { it.roomName }))
