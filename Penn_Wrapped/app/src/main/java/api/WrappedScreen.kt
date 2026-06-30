//package api
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@Composable
//fun WrappedScreen(
//    viewModel: WrappedViewModel = viewModel(),
//    semesterId: String = "Spring_2025",
//    onFinish: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        viewModel.loadExperience(context, semesterId)
//    }
//
//    when (uiState.state) {
//        ExperienceState.LOADING -> WrappedLoadingView()
//        ExperienceState.ACTIVE -> WrappedContainer(viewModel)
//        ExperienceState.FINISHED -> onFinish()
//        ExperienceState.ERROR -> {
//            WrappedErrorView(
//                errorMessage = uiState.errorMessage ?: "An unknown error occurred.",
//                onRetry = { viewModel.loadExperience(context, semesterId) },
//                onClose = onFinish
//            )
//        }
//    }
//}