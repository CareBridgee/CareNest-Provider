package com.carenest.provider.auth.di

import com.carenest.provider.auth.data.remote.auth.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.auth.google.GoogleAuthApi
import com.carenest.provider.auth.data.remote.auth.google.GoogleAuthApiImpl
import com.carenest.provider.auth.data.remote.auth.phone.DefaultAuthApi
import com.carenest.provider.auth.data.remote.auth.phone.DefaultAuthApiImpl
import com.carenest.provider.auth.data.remote.auth.phone.DefaultAuthRemoteDataSource
import com.carenest.provider.auth.data.repository.AuthRepositoryImpl
import com.carenest.provider.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindingsModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        implementation: DefaultAuthRemoteDataSource
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindDefaultAuthApi(
        implementation: DefaultAuthApiImpl
    ): DefaultAuthApi

    @Binds
    @Singleton
    abstract fun bindGoogleAuthApi(
        implementation: GoogleAuthApiImpl
    ): GoogleAuthApi
}
