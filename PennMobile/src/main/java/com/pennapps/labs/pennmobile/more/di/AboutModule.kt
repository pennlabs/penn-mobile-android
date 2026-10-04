package com.pennapps.labs.pennmobile.more.di

import com.pennapps.labs.pennmobile.more.repo.AboutRepository
import com.pennapps.labs.pennmobile.more.repo.AboutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AboutModule {
    @Binds
    @Singleton
    abstract fun bindAboutRepository(aboutRepositoryImpl: AboutRepositoryImpl): AboutRepository
}
