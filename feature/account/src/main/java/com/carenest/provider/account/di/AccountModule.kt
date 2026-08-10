package com.carenest.provider.account.di

import com.carenest.provider.account.data.remote.KtorReviewsRemoteDataSource
import com.carenest.provider.account.data.remote.ReviewsRemoteDataSource
import com.carenest.provider.account.data.repository.ReviewsRepositoryImpl
import com.carenest.provider.account.domain.repository.ReviewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountModule {

    @Binds
    @Singleton
    abstract fun bindReviewsRemoteDataSource(
        impl: KtorReviewsRemoteDataSource
    ): ReviewsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReviewsRepository(
        impl: ReviewsRepositoryImpl
    ): ReviewsRepository
}
