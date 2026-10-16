package com.pennapps.labs.pennmobile.games.repo

import com.pennapps.labs.pennmobile.games.classes.Game

interface GamesRepository {
    fun getGames(): List<Game>
}
