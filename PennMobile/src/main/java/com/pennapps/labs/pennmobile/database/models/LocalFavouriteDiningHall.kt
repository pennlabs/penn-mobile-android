package com.pennapps.labs.pennmobile.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_dining_halls")
data class LocalFavouriteDiningHall(
    @PrimaryKey
    val diningHallId: Int
)
