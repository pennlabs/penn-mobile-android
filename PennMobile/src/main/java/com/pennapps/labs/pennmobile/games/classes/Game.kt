package com.pennapps.labs.pennmobile.games.classes

data class Game(
    val id: String,
    val title: String,
    val description: String,
    val isPlayable: Boolean = false,
)
