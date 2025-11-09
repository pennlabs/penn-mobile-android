package com.pennapps.labs.pennmobile.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.pennapps.labs.pennmobile.database.models.LocalFavouriteDiningHall
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDiningHallDao: BaseDao<LocalFavouriteDiningHall> {

    @Query("SELECT * FROM favourite_dining_halls")
    abstract fun getFavoriteDiningHalls(): Flow<List<LocalFavouriteDiningHall>>

    @Query("DELETE FROM favourite_dining_halls WHERE diningHallId = :id")
    abstract suspend fun removeFavouriteDiningHall(id: Int)

}