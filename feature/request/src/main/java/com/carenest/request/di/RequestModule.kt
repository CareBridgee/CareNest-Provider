package com.carenest.request.di

import com.carenest.request.data.datasource.FakeNurseRequestsDataSource
import com.carenest.request.data.datasource.FakeVisitSummaryDataSource
import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.datasource.VisitSummaryDataSource
import com.carenest.request.data.repository.NurseRequestsRepositoryImpl
import com.carenest.request.data.repository.VisitSummaryRepositoryImpl
import com.carenest.request.domain.repository.NurseRequestsRepository
import com.carenest.request.domain.repository.VisitSummaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RequestModule {

    @Binds
    @Singleton
    abstract fun bindNurseRequestsDataSource(
        impl: FakeNurseRequestsDataSource
    ): NurseRequestsDataSource

    @Binds
    @Singleton
    abstract fun bindNurseRequestsRepository(
        impl: NurseRequestsRepositoryImpl
    ): NurseRequestsRepository

    @Binds
    @Singleton
    abstract fun bindVisitSummaryDataSource(
        impl: FakeVisitSummaryDataSource
    ): VisitSummaryDataSource

    @Binds
    @Singleton
    abstract fun bindVisitSummaryRepository(
        impl: VisitSummaryRepositoryImpl
    ): VisitSummaryRepository
}