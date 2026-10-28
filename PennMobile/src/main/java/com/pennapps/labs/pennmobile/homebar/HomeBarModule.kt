package com.pennapps.labs.pennmobile.homebar

import com.pennapps.labs.pennmobile.homebar.repo.HomeBarRepo
import com.pennapps.labs.pennmobile.homebar.repo.HomeBarRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeBarModule {
    @Binds
    @Singleton
    abstract fun bindHomeBarRepo(homeBarRepoImpl: HomeBarRepoImpl): HomeBarRepo
}
