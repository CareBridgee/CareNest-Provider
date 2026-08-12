package com.carenest.request.di

import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.data.datasource.NurseRequestsDataSourceImpl
import com.carenest.request.data.repository.NurseRequestsRepositoryImpl
import com.carenest.request.data.repository.PatientGeocodingRepositoryImpl
import com.carenest.request.data.repository.VisitSummaryRepositoryImpl
import com.carenest.request.data.remote.KtorRequestRemoteDataSource
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.domain.repository.NurseRequestsRepository
import com.carenest.request.domain.repository.PatientGeocodingRepository
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
        impl: NurseRequestsDataSourceImpl
    ): NurseRequestsDataSource

    @Binds
    @Singleton
    abstract fun bindNurseRequestsRepository(
        impl: NurseRequestsRepositoryImpl
    ): NurseRequestsRepository

    @Binds
    @Singleton
    abstract fun bindRequestRemoteDataSource(
        impl: KtorRequestRemoteDataSource
    ): RequestRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindVisitSummaryRepository(
        impl: VisitSummaryRepositoryImpl
    ): VisitSummaryRepository

    @Binds
    @Singleton
    abstract fun bindPatientGeocodingRepository(
        impl: PatientGeocodingRepositoryImpl,
    ): PatientGeocodingRepository
}
