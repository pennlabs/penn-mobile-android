package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PennMobile :
    MultiDexApplication(),
    ImageLoaderFactory,
    Configuration.Provider {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Must stay a getter. A field initializer would run before Hilt injects workerFactory.
    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

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
                    .build(),
            )
        }
    }

    override fun newImageLoader(): ImageLoader = imageLoader
}
