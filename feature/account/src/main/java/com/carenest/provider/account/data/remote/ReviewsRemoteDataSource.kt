package com.carenest.provider.account.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

interface ReviewsRemoteDataSource {
    suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): HttpResponse
}

class KtorReviewsRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
) : ReviewsRemoteDataSource {
    override suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): HttpResponse = httpClient.get("/api/v1/nurses/$nurseId/reviews") {
        parameter("page", page)
        parameter("size", size)
        parameter("sort", sort)
    }
}
