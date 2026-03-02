package com.pennapps.labs.pennmobile.gsr.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GsrViewModel
    @Inject
    constructor(
        private val repository: GsrRepo,
    ) : ViewModel() {
        private val _error = MutableStateFlow<Throwable?>(null)
        val error = _error.asStateFlow()

        private val _isBooking = MutableStateFlow(false)
        val isBooking = _isBooking.asStateFlow()

        private val _bookingSuccess = MutableStateFlow(false)
        val bookingSuccess = _bookingSuccess.asStateFlow()

        val savedUserInfo = repository.getSavedUserInfo()

        fun bookGsr(
            startTime: String?,
            endTime: String?,
            gid: Int,
            roomId: Int,
            roomName: String,
            firstName: String,
            lastName: String,
            email: String,
        ) {
            // Validation - standard check
            if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
                _error.value = Exception("Please fill in all fields before booking")
                return
            }

            viewModelScope.launch {
                _isBooking.value = true
                _error.value = null
                try {
                    repository.bookGsr(startTime, endTime, gid, roomId, roomName, firstName, lastName, email)
                    _bookingSuccess.value = true
                } catch (e: Exception) {
                    Log.e("GsrViewModel", "Booking failed", e)
                    _error.value = e
                } finally {
                    _isBooking.value = false
                }
            }
        }
    }
