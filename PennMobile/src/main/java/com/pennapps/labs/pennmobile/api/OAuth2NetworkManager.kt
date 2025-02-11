package com.pennapps.labs.pennmobile.api

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
            val result = networkManager.getAccessToken()
            if (result is NetworkResult.Success) {
               withContext(Dispatchers.Main) {
                    function.invoke(result.data)
               }
            }
        }
    }
}
