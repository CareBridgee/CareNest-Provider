package com.carenest.provider.earnings.di

import com.carenest.provider.earnings.data.repository.EarningsRepositoryImpl
import com.carenest.provider.earnings.domain.repository.EarningsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EarningsModule {

    @Binds
    @Singleton
    abstract fun bindEarningsRepository(
        impl: EarningsRepositoryImpl
    ): EarningsRepository
}
