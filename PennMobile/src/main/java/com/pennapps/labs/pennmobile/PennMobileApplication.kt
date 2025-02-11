package com.pennapps.labs.pennmobile

import androidx.multidex.MultiDexApplication
import com.pennapps.labs.pennmobile.api.NetworkContainer

class PennMobileApplication : MultiDexApplication() {
    lateinit var networkContainer: NetworkContainer

    override fun onCreate() {
        super.onCreate()
        networkContainer = NetworkContainer(this.applicationContext)
    }

}