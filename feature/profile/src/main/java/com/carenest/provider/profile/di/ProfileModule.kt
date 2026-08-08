package com.carenest.provider.profile.di

import com.carenest.provider.profile.data.remote.KtorProfileRemoteDataSource
import com.carenest.provider.profile.data.remote.ProfileRemoteDataSource
import com.carenest.provider.profile.data.repository.ProfileRepositoryImpl
import com.carenest.provider.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    abstract fun bindRemoteDataSource(implementation: KtorProfileRemoteDataSource): ProfileRemoteDataSource

    @Binds
    abstract fun bindRepository(implementation: ProfileRepositoryImpl): ProfileRepository
}
