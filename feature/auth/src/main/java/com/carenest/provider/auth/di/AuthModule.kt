package com.carenest.provider.auth.di

import com.carenest.provider.auth.data.remote.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.KtorAuthRemoteDataSource
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
        implementation: KtorAuthRemoteDataSource
    ): AuthRemoteDataSource
}
