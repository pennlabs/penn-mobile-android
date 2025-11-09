package com.pennapps.labs.pennmobile.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pennapps.labs.pennmobile.database.dao.FavoriteDiningHallDao
import com.pennapps.labs.pennmobile.database.models.LocalFavouriteDiningHall

@Database(entities = [LocalFavouriteDiningHall::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract val favouriteDiningHallsDao: FavoriteDiningHallDao

}