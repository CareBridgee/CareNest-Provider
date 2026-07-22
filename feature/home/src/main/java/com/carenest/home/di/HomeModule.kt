package com.carenest.home.di

import com.carenest.home.data.repository.FakeNurseRequestsRepository
import com.carenest.home.data.repository.NurseRequestsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeBindingsModule {
    @Binds
    @Singleton
    abstract fun bindNurseRequestsRepository(
        implementation: FakeNurseRequestsRepository,
    ): NurseRequestsRepository
}
