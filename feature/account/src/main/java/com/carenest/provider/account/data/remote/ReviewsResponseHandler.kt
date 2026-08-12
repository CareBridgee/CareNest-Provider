package com.carenest.provider.account.data.remote

import com.carenest.provider.account.data.remote.dto.ErrorResponseDto
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ReviewsResponseHandler @Inject constructor(
    private val json: Json,
) {
    suspend inline fun <reified T> bodyOrThrow(response: HttpResponse): T {
        requireSuccess(response)
        return response.body()
    }

    suspend fun requireSuccess(response: HttpResponse) {
        if (response.status.isSuccess()) return
        throw createException(response)
    }

    private suspend fun createException(response: HttpResponse): Exception {
        val statusCode = response.status.value
        val raw = runCatching { response.bodyAsText() }.getOrDefault("")
        val parsed = runCatching { json.decodeFromString<ErrorResponseDto>(raw) }.getOrNull()
        val message = parsed?.message ?: parsed?.error ?: parsed?.details

        val displayMessage = when {
            !message.isNullOrBlank() -> message
            statusCode == 403 -> "Access denied (403). You do not have permission to access these reviews."
            statusCode == 401 -> "Session expired (401). Please log in again."
            statusCode >= 500 -> "Server error ($statusCode). Please try again later."
            else -> raw.takeIf(String::isNotBlank) ?: "HTTP $statusCode (${response.status.description})"
        }
        return IllegalStateException(displayMessage)
    }
}
