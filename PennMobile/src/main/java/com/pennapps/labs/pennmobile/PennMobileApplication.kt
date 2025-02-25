package com.pennapps.labs.pennmobile

import androidx.multidex.MultiDexApplication
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.StudentLife
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PennMobileApplication : MultiDexApplication() {
    @Inject lateinit var studentLife: StudentLife
    @Inject lateinit var campusExpress: CampusExpress
    @Inject lateinit var platform: Platform

    override fun onCreate() {
        super.onCreate()
        MainActivity.initializeRetrofit(
            studentLife, platform, campusExpress
        )
    }
}