package com.pennapps.labs.pennmobile.api

import StudentLife
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Calendar

enum class AuthError {
    REFRESH_400,
}

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: Throwable) : NetworkResult<Nothing>()
}

class NetworkManager (
    val campusExpress: CampusExpress,
    val platform: Platform,
    val studentLife: StudentLife,
    val context: Context
) {
    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

     suspend inline fun <T> safeNetworkCall(crossinline request: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(request())
        } catch (e: Exception) {
            // log all network failures
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
            NetworkResult.Error(e)
        }
    }

    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private suspend fun refreshAccessToken(): NetworkResult<String> {
        val refreshToken = sp.getString(context.getString(R.string.refresh_token), "") ?: ""
        val clientID = BuildConfig.PLATFORM_CLIENT_ID

        val response = safeNetworkCall {
            studentLife.refreshAccessToken(
                refreshToken,
                "refresh_token",
                clientID,
            )
        }

        when (response) {
            is NetworkResult.Success -> {
                val t = response.data.body()

                if (response.data.isSuccessful && t != null) {
                    val editor = sp.edit()
                    editor.putString(context.getString(R.string.access_token), t.accessToken)
                    editor.putString(context.getString(R.string.refresh_token), t.refreshToken)
                    editor.putString(context.getString(R.string.expires_in), t.expiresIn)

                    val expiresIn = t.expiresIn
                    val expiresInInt = (expiresIn!!.toInt() * 1000)
                    val currentTime = Calendar.getInstance().timeInMillis
                    editor.putLong(context.getString(R.string.token_expires_at), currentTime + expiresInInt)
                    editor.apply()

                    return NetworkResult.Success("Bearer " + t.accessToken)
                } else {
                    val error =
                        Exception(
                            response.data.errorBody()?.string()
                                ?: "Unknown Error",
                        )
                    FirebaseCrashlytics.getInstance().recordException(error)
                    Log.e("NetworkManager", "Error refreshing access token: ", error)

                    if (response.data.code() == 404) {
                        authErrorChannel.send(AuthError.REFRESH_400)
                    }

                    return NetworkResult.Error(error)
                }
            }

            is NetworkResult.Error -> {
                return response
            }
        }
    }

    suspend fun getAccessToken(): NetworkResult<String> {
        val guestMode = sp.getBoolean(context.getString(R.string.guest_mode), false)
        if (guestMode) {
            return NetworkResult.Error(Exception("Tried to call getAccessToken in guest mode"))
        }

        return tokenMutex.withLock {
            val expiresIn = sp.getString(context.getString(R.string.expires_in), "")
            val expiresAt = sp.getLong(context.getString(R.string.token_expires_at), 0)
            val currentTime = Calendar.getInstance().timeInMillis

            if (expiresIn.isNullOrEmpty() || currentTime >= expiresAt) {
                Log.i("NetworkManager", "Expired")
                refreshAccessToken()
            } else {
                Log.i("NetworkManager", "Not Expired")
                val bearerToken = "Bearer " + sp.getString(context.getString(R.string.access_token), "").toString()
                NetworkResult.Success(bearerToken)
            }
        }
    }

    companion object {
        val tokenMutex = Mutex()
        val authErrorChannel = Channel<AuthError>()
    }
}