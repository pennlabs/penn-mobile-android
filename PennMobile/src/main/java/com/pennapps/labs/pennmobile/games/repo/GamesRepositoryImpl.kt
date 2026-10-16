package com.pennapps.labs.pennmobile.games.repo

import com.pennapps.labs.pennmobile.games.classes.Game
import javax.inject.Inject

/**
 * Static placeholder list until real games exist. When games come from the backend, make
 * [getGames] a suspend function returning `Result<List<Game>>` (see compose/utils/Result.kt).
 */
class GamesRepositoryImpl
    @Inject
    constructor() : GamesRepository {
        override fun getGames(): List<Game> = games

        private companion object {
            val games =
                listOf(
                    Game(
                        id = "placeholder",
                        title = "Coming soon",
                        description = "Games are on the way.",
                    ),
                )
        }
    }
