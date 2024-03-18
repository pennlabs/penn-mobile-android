package com.pennapps.labs.pennmobile

import android.app.Application
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.NetworkContainer

class PennMobileApp : Application() {
    val networkContainer = NetworkContainer()
}