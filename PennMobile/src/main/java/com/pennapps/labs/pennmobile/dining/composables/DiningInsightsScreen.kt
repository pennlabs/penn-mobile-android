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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.pennapps.labs.pennmobile.compose.presentation.components.error.ErrorCard
import com.pennapps.labs.pennmobile.compose.presentation.components.error.UserDisplayErrors
import com.pennapps.labs.pennmobile.dining.adapters.DiningInsightsCardAdapter.Companion.DAYS_IN_SEMESTER
import com.pennapps.labs.pennmobile.dining.adapters.DiningInsightsCardAdapter.Companion.START_DAY_OF_SEMESTER
import com.pennapps.labs.pennmobile.dining.composables.components.AprilFoolsCard
import com.pennapps.labs.pennmobile.dining.composables.components.BudgetPlanCard
import com.pennapps.labs.pennmobile.dining.composables.components.DiningBalancesCard
import com.pennapps.labs.pennmobile.dining.composables.components.DiningBudgetingCard
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

    val showPranks by viewModel.showAprilPranks.collectAsState()
    val budget by viewModel.budget.collectAsStateWithLifecycle()
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
        val plannedSwipes by viewModel.plannedSwipesPerWeek.collectAsState()
        val plannedDollars by viewModel.plannedDollarsPerWeek.collectAsState()

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
                val balances = cell.diningBalances

                val diningDollars =
                    cell.diningBalances?.diningDollars?.let { dollars ->
                        "$${if (showPranks) "-" else ""}$dollars"
                    } ?: "0.00"

                val regularVisits =
                    cell.diningBalances?.regularVisits?.let { visits ->
                        if (showPranks) -visits else visits
                    } ?: 0

                val guestVisits =
                    cell.diningBalances?.guestVisits?.let {
                        if (showPranks) -it else it
                    } ?: 0

                DiningBalancesCard(
                    diningDollars = diningDollars,
                    swipes = regularVisits,
                    guestSwipes = guestVisits,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                budget?.let {
                    Text(
                        text = "Dining Budgeting",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    Text(
                        text = "Weeks Left:" + viewModel.weeksLeft,
                        fontFamily = GilroyExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    DiningBudgetingCard(
                        diningDollars = "$%.2f".format(it.dollarsPerWeek),
                        swipes = it.swipesPerWeek,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                Text(
                    text = "Want to Spend",
                    fontFamily = GilroyExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                BudgetPlanCard(
                    maxDollars = balances?.diningDollars?.toString()?.toDoubleOrNull() ?: 0.0,
                    maxSwipes = balances?.regularVisits ?: 0,
                    onDollarsChange = { viewModel.setPlannedDollars(it) },
                    onSwipesChange = { viewModel.setPlannedSwipes(it?.toInt()) },
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            if (viewModel.isAprilFoolsDay) {
                item {
                    AprilFoolsCard(
                        allowPrank = showPranks,
                        onPrankListener = { viewModel.setAprilPranks(it) },
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
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
                            showPranks = showPranks,
                            plannedPerWeek = plannedDollars,
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
                            showPranks = showPranks,
                            plannedPerWeek = plannedSwipes?.toDouble(),
                        )
                    }
                }
            }
        }
    }
}
