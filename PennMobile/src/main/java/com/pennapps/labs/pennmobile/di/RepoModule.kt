package com.pennapps.labs.pennmobile.di

import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import com.pennapps.labs.pennmobile.dining.repo.DiningRepoImpl
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindingDiningRepo(diningRepoImpl: DiningRepoImpl): DiningRepo

    @Binds
    @Singleton
    abstract fun bindGsrRepo(gsrRepoImpl: GsrRepoImpl): GsrRepo
}
