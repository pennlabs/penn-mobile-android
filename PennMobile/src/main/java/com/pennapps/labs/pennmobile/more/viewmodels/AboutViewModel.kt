package com.pennapps.labs.pennmobile.more.viewmodels

import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.more.classes.TeamMember
import com.pennapps.labs.pennmobile.more.repo.AboutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AboutUiState(
    val members: List<TeamMember> = emptyList(),
    val alumni: List<TeamMember> = emptyList(),
    val pennLabsUrl: String = PENN_LABS_URL,
)

const val PENN_LABS_URL = "https://pennlabs.org"

@HiltViewModel
class AboutViewModel
    @Inject
    constructor(
        repository: AboutRepository,
    ) : ViewModel() {
        private val _uiState =
            MutableStateFlow(
                AboutUiState(
                    members = repository.getMembers(),
                    alumni = repository.getAlumni(),
                ),
            )
        val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
    }
