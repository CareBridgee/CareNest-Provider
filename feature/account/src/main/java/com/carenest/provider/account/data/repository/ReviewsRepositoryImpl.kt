package com.carenest.provider.account.data.repository

import com.carenest.provider.account.data.remote.ReviewsRemoteDataSource
import com.carenest.provider.account.data.remote.dto.NurseReviewDto
import com.carenest.provider.account.data.remote.dto.ReviewsPageResponseDto
import com.carenest.provider.account.domain.model.NurseReview
import com.carenest.provider.account.domain.model.NurseReviewsPage
import com.carenest.provider.account.domain.repository.ReviewsRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class ErrorResponseDto(
    val message: String? = null,
    val error: String? = null,
    val details: String? = null,
)

class ReviewsRepositoryImpl @Inject constructor(
    private val dataSource: ReviewsRemoteDataSource,
    private val json: Json,
) : ReviewsRepository {

    override suspend fun getNurseReviews(
        nurseId: String,
        page: Int,
        size: Int,
        sort: String,
    ): Result<NurseReviewsPage> = execute {
        val response = dataSource.getNurseReviews(nurseId, page, size, sort)
        val dto = response.successBody<ReviewsPageResponseDto>()
        dto.toDomain()
    }

    private suspend inline fun <T> execute(crossinline block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (error: Exception) {
            Result.failure(error)
        }

    private suspend inline fun <reified T> HttpResponse.successBody(): T {
        if (!status.isSuccess()) throw backendException()
        return body()
    }

    private suspend fun HttpResponse.backendException(): Exception {
        val statusCode = status.value
        val raw = runCatching { bodyAsText() }.getOrDefault("")
        val parsed = runCatching { json.decodeFromString<ErrorResponseDto>(raw) }.getOrNull()
        val message = parsed?.message ?: parsed?.error ?: parsed?.details

        val displayMessage = when {
            !message.isNullOrBlank() -> message
            statusCode == 403 -> "Access denied (403). You do not have permission to access these reviews."
            statusCode == 401 -> "Session expired (401). Please log in again."
            statusCode >= 500 -> "Server error ($statusCode). Please try again later."
            else -> raw.takeIf(String::isNotBlank) ?: "HTTP $statusCode (${status.description})"
        }
        return IllegalStateException(displayMessage)
    }
}

private fun ReviewsPageResponseDto.toDomain(): NurseReviewsPage = NurseReviewsPage(
    totalElements = totalElements,
    totalPages = totalPages,
    isFirst = first,
    isLast = last,
    pageNumber = number,
    pageSize = size,
    reviews = content.map { it.toDomain() },
)

private fun NurseReviewDto.toDomain(): NurseReview = NurseReview(
    id = id,
    bookingId = bookingId,
    profileId = profileId,
    nurseId = nurseId,
    rating = rating,
    reviewText = reviewText.orEmpty(),
    isAnonymous = isAnonymous,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
