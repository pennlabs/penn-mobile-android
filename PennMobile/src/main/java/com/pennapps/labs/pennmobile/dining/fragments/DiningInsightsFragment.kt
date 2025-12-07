package com.pennapps.labs.pennmobile.dining.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.fragments.CampusExpressLoginFragment
import com.pennapps.labs.pennmobile.dining.composables.DiningInsightsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiningInsightsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_dining_insights, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.dining_insights_compose_view)
        val fragmentContainer = view.findViewById<FrameLayout>(R.id.dining_insights_container)

        composeView.setContent {
            DiningInsightsScreen(
                onLoginRequired = {
                    fragmentContainer.visibility = View.VISIBLE
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.dining_insights_container, CampusExpressLoginFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit()
                },
            )
        }

        return view
    }
}
