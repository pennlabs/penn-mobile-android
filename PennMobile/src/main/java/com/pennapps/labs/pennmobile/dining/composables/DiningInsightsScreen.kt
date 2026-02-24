package com.pennapps.labs.pennmobile.dining.composables

import GilroyExtraBold
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.pennapps.labs.pennmobile.compose.presentation.components.error.ErrorCard
import com.pennapps.labs.pennmobile.compose.presentation.components.error.UserDisplayErrors
import com.pennapps.labs.pennmobile.dining.composables.components.DiningBalancesCard
import com.pennapps.labs.pennmobile.dining.composables.components.DiningPredictionCard
import com.pennapps.labs.pennmobile.dining.viewmodels.DiningInsightsViewModel
import com.pennapps.labs.pennmobile.ui.theme.PennMobileTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun DiningInsightsScreen(
    onLoginRequirement: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiningInsightsViewModel =
        hiltViewModel(
            checkNotNull(
                LocalViewModelStoreOwner.current,
            ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            },
            null,
        ),
) {
    val currentOnLoginRequirement by rememberUpdatedState(onLoginRequirement)

    PennMobileTheme {
        LaunchedEffect(Unit) {
            viewModel.checkTokenAndFetch()
        }

        val loginRequired by viewModel.loginRequired.collectAsState()
        LaunchedEffect(loginRequired) {
            if (loginRequired) {
                currentOnLoginRequirement()
            }
        }

        val cells by viewModel.cells.collectAsState()
        val pastBalances by viewModel.pastBalances.collectAsState()

        LazyColumn(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 64.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Log.d("DiningInsightsScreen", "cells: $cells")

            // Header for Dining Balance
            item {
                Text(
                    text = "Dining Balance",
                    fontFamily = GilroyExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            // Dining Balance cards
            items(cells.filter { it.type == "dining_balance" }) { cell ->
                DiningBalancesCard(
                    diningDollars = "$${cell.diningBalances?.diningDollars ?: "0.00"}",
                    swipes = cell.diningBalances?.regularVisits ?: 0,
                    guestSwipes = cell.diningBalances?.guestVisits ?: 0,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            /*
             * When the pastBalances list size is:
             * a) 0 -> The user is either a junior/senior not on a dining plan or an RA who has a plan but for RAs, we never get the plan balances at all
             * b) 1 -> The user is a normal student on a plan. However, Campus Express currently has an issue
             * c) 2 or more -> Everything is alright. Render the balances graph.
             */
            pastBalances?.diningBalancesList?.size?.let { balanceListSize ->
                if (balanceListSize == 0 || balanceListSize == 1) {
                    item {
                        ErrorCard(
                            modifier =
                                Modifier
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth(0.95f),
                            errorMessage =
                                when (balanceListSize) {
                                    0 -> UserDisplayErrors.PAST_BALANCES_NOT_AVAILABLE
                                    1 -> UserDisplayErrors.CAMPUS_EXPRESS_DOWN
                                    else -> "Error occurred"
                                },
                        )
                    }
                } else {
                    // Header for Dining Dollars Predictions
                    item {
                        Text(
                            text = "Dining Dollars Predictions",
                            fontFamily = GilroyExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    // Dining Dollars Prediction cards
                    items(cells.filter { it.type == "dining_dollars_predictions" }) { cell ->
                        DiningPredictionCard(
                            cell = cell,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }

                    // Header for Swipes Predictions
                    item {
                        Text(
                            text = "Swipes Predictions",
                            fontFamily = GilroyExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    // Swipes Prediction cards
                    items(cells.filter { it.type == "dining_swipes_predictions" }) { cell ->
                        DiningPredictionCard(
                            cell = cell,
                        )
                    }
                }
            }
        }
    }
}
