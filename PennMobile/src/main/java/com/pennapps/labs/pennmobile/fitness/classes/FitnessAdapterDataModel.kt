package com.pennapps.labs.pennmobile.fitness.classes

interface FitnessAdapterDataModel {
    fun flipState(roomId: Int): Boolean

    fun getNumber(isFavorite: Boolean): Int

    fun getRoom(
        isFavorite: Boolean,
        position: Int,
    ): FitnessRoom

    fun getTot(): Int

    fun getRoomAll(roomId: Int): FitnessRoom

    fun isFavorite(roomId: Int): Boolean
}
