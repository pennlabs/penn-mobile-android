package com.pennapps.labs.pennmobile.more.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.more.compose.AboutScreen
import com.pennapps.labs.pennmobile.more.viewmodels.AboutViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mActivity.hideBottomBar()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // TODO: When we do a version upgrade for the fragment and lifecycle deps, update this to use the 'by viewModels() instead'
                // hiltViewModel() rather than `by viewModels()`: fragment 1.5.1 + lifecycle 2.9
                //      crash on Hilt VMs with missing CreationExtras.
                val viewModel: AboutViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()

                AppTheme {
                    AboutScreen(
                        state = state,
                        onBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                        onLearnMoreClick = {
                            startActivity(Intent(Intent.ACTION_VIEW, state.pennLabsUrl.toUri()))
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.about)
        mActivity.setSelectedTab(MainActivity.MORE)
    }
}
