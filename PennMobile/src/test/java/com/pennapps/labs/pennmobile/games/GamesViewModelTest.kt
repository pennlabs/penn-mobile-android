package com.pennapps.labs.pennmobile.games

import com.pennapps.labs.pennmobile.games.classes.Game
import com.pennapps.labs.pennmobile.games.repo.GamesRepository
import com.pennapps.labs.pennmobile.games.viewmodels.GamesViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesViewModelTest {
    private class FakeGamesRepository(
        private val games: List<Game>,
    ) : GamesRepository {
        override fun getGames() = games
    }

    @Test
    fun `ui state mirrors the repository`() {
        val games = listOf(Game("a", "A", "First"), Game("b", "B", "Second"))

        val state = GamesViewModel(FakeGamesRepository(games)).uiState.value

        assertEquals(games, state.games)
    }

    @Test
    fun `empty repository yields empty list`() {
        val state = GamesViewModel(FakeGamesRepository(emptyList())).uiState.value

        assertTrue(state.games.isEmpty())
    }
}
