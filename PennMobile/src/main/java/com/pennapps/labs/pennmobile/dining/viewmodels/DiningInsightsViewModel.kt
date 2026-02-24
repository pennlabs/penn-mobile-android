package com.pennapps.labs.pennmobile.dining.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.CampusExpressNetworkManager
import com.pennapps.labs.pennmobile.dining.adapters.DiningInsightsCardAdapter
import com.pennapps.labs.pennmobile.dining.classes.DiningBalances
import com.pennapps.labs.pennmobile.dining.classes.DiningBalancesList
import com.pennapps.labs.pennmobile.dining.classes.DiningInsightCell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DiningInsightsViewModel
    @Inject
    constructor(
        private val api: CampusExpress,
        private val tokenManager: CampusExpressNetworkManager,
    ) : ViewModel() {
        private val _currentBalances = MutableStateFlow<DiningBalances?>(null)
        val currentBalances: StateFlow<DiningBalances?> = _currentBalances.asStateFlow()

        private val _pastBalances = MutableStateFlow<DiningBalancesList?>(null)
        val pastBalances: StateFlow<DiningBalancesList?> = _pastBalances.asStateFlow()

        private val _error = MutableStateFlow<Throwable?>(null)
        val error: StateFlow<Throwable?> = _error.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        private val _loginRequired = MutableStateFlow(false)
        val loginRequired: StateFlow<Boolean> = _loginRequired.asStateFlow()

        fun checkTokenAndFetch() {
            val token = tokenManager.getAccessToken()
            if (token.isNullOrEmpty()) {
                _loginRequired.value = true
            } else {
                fetchDiningBalances(token)
            }
        }

        fun fetchDiningBalances(token: String) {
            viewModelScope.launch {
                _isRefreshing.value = true
                val bearer = "Bearer $token"
                Log.d("DiningInsightsViewModel", "fetchDiningBalances: $bearer")
                try {
                    Log.d("DiningInsightsViewModel", "Attempting getting value")
                    _currentBalances.value = api.getCurrentDiningBalances(bearer)

                    Log.d("DiningInsightsViewModel", "Dining Dollars: ${_currentBalances.value?.diningDollars}")
                    val now = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    _pastBalances.value =
                        api.getPastDiningBalances(
                            bearer,
                            DiningInsightsCardAdapter.START_DAY_OF_SEMESTER,
                            now.format(formatter),
                        )
                    Log.d("DiningInsightsViewModel", "Past Balances: ${_pastBalances.value?.diningBalancesList?.size}")
                } catch (e: Exception) {
                    Log.d("DiningInsightsViewModel", "Failed catch getting value")
                    Log.d("DiningInsightsViewModel", "Error: $e")
                    _error.value = e
                } finally {
                    Log.d("DiningInsightsViewModel", "Cooked Attempting getting value")
                    _isRefreshing.value = false
                }
            }
        }

        val cells: StateFlow<List<DiningInsightCell>> =
            combine(
                currentBalances,
                pastBalances,
            ) { current, past ->
                val result = mutableListOf<DiningInsightCell>()

                current?.let {
                    result.add(
                        DiningInsightCell().apply {
                            type = "dining_balance"
                            diningBalances = it
                        },
                    )
                }

                past?.let {
                    result.add(
                        DiningInsightCell().apply {
                            type = "dining_dollars_predictions"
                            diningBalances = current
                            diningBalancesList = it
                        },
                    )
                    result.add(
                        DiningInsightCell().apply {
                            type = "dining_swipes_predictions"
                            diningBalances = current
                            diningBalancesList = it
                        },
                    )
                }

                Log.d("DiningInsightsViewModel", "cells: $result")

                result
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                emptyList(),
            )
    }
