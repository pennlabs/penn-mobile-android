package com.pennapps.labs.pennmobile.studentresources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme

class StudentResourcesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                AppTheme {
                    StudentResourcesScreen(
                        onBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                    )
                }
            }
        }
}
