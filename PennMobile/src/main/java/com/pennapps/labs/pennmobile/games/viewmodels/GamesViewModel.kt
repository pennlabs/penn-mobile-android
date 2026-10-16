package com.pennapps.labs.pennmobile.games.viewmodels

import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.games.classes.Game
import com.pennapps.labs.pennmobile.games.repo.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class GamesUiState(
    val games: List<Game> = emptyList(),
)

@HiltViewModel
class GamesViewModel
    @Inject
    constructor(
        repository: GamesRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(GamesUiState(games = repository.getGames()))
        val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()
    }
