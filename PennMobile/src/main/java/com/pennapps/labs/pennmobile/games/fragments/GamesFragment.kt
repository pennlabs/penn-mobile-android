package com.pennapps.labs.pennmobile.games.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.games.compose.GamesScreen
import com.pennapps.labs.pennmobile.games.viewmodels.GamesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GamesFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
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
                // hiltViewModel() rather than `by viewModels()`: fragment 1.5.1 + lifecycle 2.9
                //      crash on Hilt VMs with missing CreationExtras. Same as AboutFragment.
                val viewModel: GamesViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()

                AppTheme {
                    GamesScreen(
                        state = state,
                        onBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                        // TODO: open the selected game once games exist
                        onGameClick = {},
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.games)
        mActivity.setSelectedTab(MainActivity.HOME)
    }
}
