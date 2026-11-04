package com.pennapps.labs.pennmobile.games.repo

import com.pennapps.labs.pennmobile.games.classes.Game
import javax.inject.Inject

/**
 * Local catalog. Keep games disabled until their playable destination exists. When games come from the backend, make
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
                        id = "word_hunt",
                        title = "Word Hunt",
                        description = "Find Penn words by connecting letters",
                    ),
                )
        }
    }
