package com.carenest.home.di

import com.carenest.home.data.datasource.FakeNurseRequestsDataSource
import com.carenest.home.data.datasource.NurseRequestsDataSource
import com.carenest.home.data.repository.NurseRequestsRepositoryImpl
import com.carenest.home.domain.repository.NurseRequestsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeBindingsModule {

    @Binds
    abstract fun bindNurseRequestsDataSource(
        impl: FakeNurseRequestsDataSource,
    ): NurseRequestsDataSource

    @Binds
    abstract fun bindNurseRequestsRepository(
        impl: NurseRequestsRepositoryImpl,
    ): NurseRequestsRepository
}
