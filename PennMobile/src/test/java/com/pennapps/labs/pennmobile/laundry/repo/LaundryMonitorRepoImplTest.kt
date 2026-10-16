package com.pennapps.labs.pennmobile.laundry.repo

import com.pennapps.labs.pennmobile.compose.utils.Result
import org.junit.Assert.assertEquals
import org.junit.Test

class LaundryMonitorRepoImplTest {
    @Test
    fun `one failed room does not hide availability in another`() {
        val result =
            summarizeRoomAvailability(
                listOf(Result.Error("failed"), Result.Success(true)),
            )

        assertEquals(Result.Success(true), result)
    }

    @Test
    fun `one failed room does not make loaded busy rooms an error`() {
        val result =
            summarizeRoomAvailability(
                listOf(Result.Error("failed"), Result.Success(false)),
            )

        assertEquals(Result.Success(false), result)
    }

    @Test
    fun `failure to load every room is an error`() {
        val result =
            summarizeRoomAvailability(
                listOf(Result.Error("first"), Result.Error("second")),
            )

        assertEquals(Result.Error("Could not load your favourite laundry rooms."), result)
    }

    @Test
    fun `no favourite rooms means nothing is available`() {
        assertEquals(Result.Success(false), summarizeRoomAvailability(emptyList()))
    }
}
