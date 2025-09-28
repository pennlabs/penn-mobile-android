package com.pennapps.labs.pennmobile.dining.composables

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.composables.components.DiningBalancesCard
import com.pennapps.labs.pennmobile.dining.composables.components.DiningPredictionCard
import com.pennapps.labs.pennmobile.dining.viewmodels.DiningInsightsViewModel

@Composable
fun DiningInsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: DiningInsightsViewModel = hiltViewModel(),
    onLoginRequired: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.checkTokenAndFetch()
    }

    val loginRequired by viewModel.loginRequired.collectAsState()
    LaunchedEffect(loginRequired) {
        if (loginRequired) {
            onLoginRequired()
        }
    }

    val cells by viewModel.cells.collectAsState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 64.dp // 👈 extra space at the bottom
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Log.d("DiningInsightsScreen", "cells: $cells")

        // Header for Dining Balance
        item {
            Text(
                text = "Dining Balance",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            )
        }

        // Dining Balance cards
        items(cells.filter { it.type == "dining_balance" }) { cell ->
            DiningBalancesCard(
                diningDollars = "$${cell.diningBalances?.diningDollars ?: "0.00"}",
                swipes = cell.diningBalances?.regularVisits ?: 0,
                guestSwipes = cell.diningBalances?.guestVisits ?: 0,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Header for Dining Dollars Predictions
        item {
            Text(
                text = "Dining Dollars Predictions",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Dining Dollars Prediction cards
        items(cells.filter { it.type == "dining_dollars_predictions" }) { cell ->
            DiningPredictionCard(
                title = "Dining Dollars Predictions",
                cell = cell,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Header for Swipes Predictions
        item {
            Text(
                text = "Swipes Predictions",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Swipes Prediction cards
        items(cells.filter { it.type == "dining_swipes_predictions" }) { cell ->
            DiningPredictionCard(
                title = "Swipes Predictions",
                cell = cell
            )
        }
    }
}
