package com.pennapps.labs.pennmobile.laundry.repo

import android.content.SharedPreferences
import android.util.Log
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class LaundryMonitorRepoImpl
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
        private val studentLife: StudentLife,
        private val oAuth2NetworkManager: OAuth2NetworkManager,
    ) : LaundryMonitorRepo {
        private val _monitorMode =
            MutableStateFlow(
                MonitorMode.fromPrefValue(sharedPreferences.getString(KEY_MONITOR_MODE, null)),
            )
        override val monitorMode: StateFlow<MonitorMode> = _monitorMode

        override fun setMonitorMode(mode: MonitorMode) {
            sharedPreferences.edit().putString(KEY_MONITOR_MODE, mode.prefValue).apply()
            _monitorMode.value = mode
        }

        override suspend fun isMachineAvailable(mode: MonitorMode): Result<Boolean> {
            if (mode == MonitorMode.OFF) {
                return Result.Success(false)
            }

            return try {
                val accessToken =
                    oAuth2NetworkManager.getAccessToken()
                        ?: return Result.Error(LOG_IN_TO_MONITOR)

                val prefResponse = studentLife.getLaundryPref("Bearer $accessToken")
                if (!prefResponse.isSuccessful) {
                    return Result.Error("Could not load your favourite laundry rooms.")
                }
                val roomIds = prefResponse.body()?.rooms.orEmpty()

                Result.Success(roomIds.any { hasOpenMachine(it, mode) })
            } catch (httpException: HttpException) {
                if (httpException.code() == 403) {
                    Result.Error(LOG_IN_TO_MONITOR)
                } else {
                    Result.Error("Server returned an error: ${httpException.code()}.")
                }
            } catch (e: UnknownHostException) {
                // Specifically catches the error for when the device is offline (no internet)
                Log.e(TAG, "Device offline.", e)
                Result.Error("You are offline. Please check your internet connection.")
            } catch (e: IOException) {
                // Catches other network I/O issues, like a timeout or connection reset
                Log.e(TAG, "Network IO error.", e)
                Result.Error("A network error occurred. Please try again.")
            } catch (e: Exception) {
                // A general catch-all for any other unexpected exceptions
                Log.e(TAG, "An unexpected error occurred.", e)
                Result.Error("An unexpected error occurred.")
            }
        }

        /**
         * One room failing should not hide a free machine in another, so this swallows per-room
         * errors. A failure that affects every room surfaces from [isMachineAvailable] instead.
         */
        private suspend fun hasOpenMachine(
            roomId: Int,
            mode: MonitorMode,
        ): Boolean =
            try {
                val roomResponse = studentLife.room(roomId)
                val machines = roomResponse.body()?.machines
                val machineList = if (mode == MonitorMode.WASHERS) machines?.washers else machines?.dryers
                roomResponse.isSuccessful && (machineList?.open ?: 0) > 0
            } catch (e: Exception) {
                Log.e(TAG, "Could not load laundry room $roomId.", e)
                false
            }

        companion object {
            private const val TAG = "LaundryMonitorRepoImpl"
            private const val KEY_MONITOR_MODE = "laundry_monitor_mode"
            private const val LOG_IN_TO_MONITOR = "Log in to monitor laundry machines."
        }
    }
