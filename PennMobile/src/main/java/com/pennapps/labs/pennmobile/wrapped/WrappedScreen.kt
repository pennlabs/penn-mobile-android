package com.pennapps.labs.pennmobile.wrapped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WrappedScreen(
    viewModel: WrappedViewModel = viewModel(),
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadExperience(context)
    }

    when (uiState.state) {
        ExperienceState.LOADING -> WrappedLoadingView()
        ExperienceState.ACTIVE -> WrappedContainer(viewModel, onClose = onFinish)
        ExperienceState.FINISHED -> onFinish()
        ExperienceState.ERROR -> {
            WrappedErrorView(
                errorMessage = uiState.errorMessage ?: "An unknown error occurred.",
                onRetry = { viewModel.loadExperience(context) },
                onClose = onFinish
            )
        }
    }
}
