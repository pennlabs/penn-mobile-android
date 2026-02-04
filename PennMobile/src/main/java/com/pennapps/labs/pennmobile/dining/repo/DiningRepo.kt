package com.pennapps.labs.pennmobile.dining.repo

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DiningRepo {
    val favouriteDiningHalls: Flow<List<Int>>
    val allDiningHalls: StateFlow<List<DiningHall>>

    suspend fun fetchFavouriteDiningHalls()

    suspend fun fetchAllDiningHalls()

    suspend fun addToFavouriteDiningHalls(id: Int): Result<Unit>

    suspend fun removeFromFavouriteDiningHalls(id: Int): Result<Unit>
}
