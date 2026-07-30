package com.carenest.provider.payouts.di

import com.carenest.provider.payouts.data.repository.PayoutsRepositoryImpl
import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PayoutsModule {

    @Binds
    @Singleton
    abstract fun bindPayoutsRepository(
        impl: PayoutsRepositoryImpl
    ): PayoutsRepository
}
