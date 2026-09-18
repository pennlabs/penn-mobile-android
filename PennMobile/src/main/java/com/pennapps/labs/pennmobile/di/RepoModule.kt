package com.pennapps.labs.pennmobile.di

import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import com.pennapps.labs.pennmobile.dining.repo.DiningRepoImpl
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepo
import com.pennapps.labs.pennmobile.gsr.repo.GsrRepoImpl
import com.pennapps.labs.pennmobile.laundry.notification.AndroidLaundryNotifier
import com.pennapps.labs.pennmobile.laundry.notification.LaundryNotifier
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepoImpl
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

    // Singleton because it owns the monitor mode StateFlow the UI observes
    @Binds
    @Singleton
    abstract fun bindLaundryMonitorRepo(laundryMonitorRepoImpl: LaundryMonitorRepoImpl): LaundryMonitorRepo

    @Binds
    abstract fun bindLaundryNotifier(androidLaundryNotifier: AndroidLaundryNotifier): LaundryNotifier
}