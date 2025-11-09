package com.pennapps.labs.pennmobile.di

import android.content.Context
import android.content.SharedPreferences
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import com.pennapps.labs.pennmobile.dining.repo.DiningRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepoModule {
//
//    @Binds
//    abstract fun bindingDiningRepo(diningRepoImpl: DiningRepoImpl): DiningRepo
//
//}

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun providesDiningRepo(
        sharedPreferences: SharedPreferences,
        @ApplicationContext applicationContext: Context,
        @MainScope mainScope: CoroutineScope,
        studentLife: StudentLife,
        oAuth2NetworkManager: OAuth2NetworkManager
    ): DiningRepo =
        DiningRepoImpl(
            sharedPreferences,
            applicationContext,
            mainScope,
            studentLife,
            oAuth2NetworkManager
        )

}