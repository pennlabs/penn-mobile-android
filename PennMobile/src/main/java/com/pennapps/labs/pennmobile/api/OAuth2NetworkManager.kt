package com.pennapps.labs.pennmobile.api

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OAuth2NetworkManager(
    private val networkManager: NetworkManager
) {
    @Deprecated("Old method of getting device id. Use network manager getDeviceId instead")
    fun getDeviceId(): String {
        return networkManager.getDeviceId()
    }

    @Deprecated("Old method of getting access tokens. Use network manager instead")
    fun getAccessToken(function: (token: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = when (val result = networkManager.getAccessToken()) {
                is NetworkResult.Success -> result.data
                else -> ""
            }

            // This is so bad it makes me want to cry. PLEASE IN THE FUTURE DO NOT CALL THIS
            // METHOD IF YOU ARE IN GUEST MODE
            withContext(Dispatchers.Main) {
                // NOTE: This is actually quite dangerous because the coroutine is not
                // lifecycle aware. There is a reason why this is being deprecated.
                try {
                    function.invoke(token)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
            }
        }
    }
}
