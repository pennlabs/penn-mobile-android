package com.pennapps.labs.pennmobile.dining.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pennapps.labs.pennmobile.api.fragments.CampusExpressLoginFragment
import com.pennapps.labs.pennmobile.dining.composables.DiningInsightsScreen
import com.pennapps.labs.pennmobile.dining.viewmodels.DiningInsightsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pennapps.labs.pennmobile.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiningInsightsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DiningInsightsScreen(
                    onLoginRequired = {
                        parentFragmentManager
                            .beginTransaction()
                            .replace(R.id.dining_insights_page, CampusExpressLoginFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack("DiningInsightsFragment")
                            .commit()
                    }
                )
            }
        }
    }
}
