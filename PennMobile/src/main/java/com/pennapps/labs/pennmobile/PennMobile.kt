package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.multidex.MultiDexApplication

class PennMobile : MultiDexApplication() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Log.d("StrictMode", "VM policy set")
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy
                    .Builder()
                    .detectUnsafeIntentLaunch()
                    .penaltyLog()
                    .penaltyDeath()
                    .build(),
            )
        }
    }
}
