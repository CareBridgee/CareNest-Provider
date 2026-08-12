package com.carenest.provider.account.data.repository

import com.carenest.provider.account.data.mapper.toDomain
import com.carenest.provider.account.data.remote.ReviewsRemoteDataSource
import com.carenest.provider.account.domain.model.NurseReviewsPage
import com.carenest.provider.account.domain.repository.ReviewsRepository
import javax.inject.Inject

class ReviewsRepositoryImpl @Inject constructor(
    private val dataSource: ReviewsRemoteDataSource,
) : ReviewsRepository {

    override suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): Result<NurseReviewsPage> = execute {
        dataSource.getNurseReviews(nurseId, page, size, sort).toDomain()
    }

    private suspend inline fun <T> execute(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: Exception) {
            Result.failure(error)
        }
}
