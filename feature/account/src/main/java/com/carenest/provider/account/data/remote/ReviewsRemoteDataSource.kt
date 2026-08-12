package com.carenest.provider.account.data.remote

import com.carenest.provider.account.data.remote.dto.ReviewsPageResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

interface ReviewsRemoteDataSource {
    suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): ReviewsPageResponseDto
}

class KtorReviewsRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val responseHandler: ReviewsResponseHandler,
) : ReviewsRemoteDataSource {
    override suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): ReviewsPageResponseDto =
        responseHandler.bodyOrThrow(
            httpClient.get("/api/v1/nurses/$nurseId/reviews") {
                parameter("page", page)
                parameter("size", size)
                parameter("sort", sort)
            },
        )
}
