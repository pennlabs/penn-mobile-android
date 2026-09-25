package com.pennapps.labs.pennmobile.gsr.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GsrReservationsViewModel
    @Inject
    constructor(
        private val repository: GsrRepo,
    ) : ViewModel() {
        private val _error = MutableStateFlow<Throwable?>(null)
        val error = _error.asStateFlow()

        private val _isCancelling = MutableStateFlow(false)
        val isCancelling = _isCancelling.asStateFlow()

        private val _cancelSuccess = MutableStateFlow(false)
        val cancelSuccess = _cancelSuccess.asStateFlow()

        private val _reservations = MutableStateFlow<List<GSRReservation>>(emptyList())
        val reservations = _reservations.asStateFlow()

        private val _isLoadingReservations = MutableStateFlow(false)
        val isLoadingReservations = _isLoadingReservations.asStateFlow()

        fun fetchReservations() {
            viewModelScope.launch {
                _isLoadingReservations.value = true
                _error.value = null
                try {
                    val result = repository.getReservations()
                    _reservations.value = result.sortedBy { it.fromDate }
                } catch (e: Exception) {
                    Log.e("GsrReservationsViewModel", "Fetching reservations failed", e)
                    _error.value = e
                } finally {
                    _isLoadingReservations.value = false
                }
            }
        }

        fun cancelGsr(
            bookingId: String?,
            isHuntsmanReservation: Boolean,
        ) {
            viewModelScope.launch {
                _isCancelling.value = true
                _error.value = null
                try {
                    repository.cancelGsr(bookingId, isHuntsmanReservation)
                    _cancelSuccess.value = true
                } catch (e: Exception) {
                    Log.e("GsrReservationsViewModel", "Cancellation failed", e)
                    _error.value = e
                } finally {
                    _isCancelling.value = false
                }
            }
        }

        fun resetCancelSuccess() {
            _cancelSuccess.value = false
        }
    }
