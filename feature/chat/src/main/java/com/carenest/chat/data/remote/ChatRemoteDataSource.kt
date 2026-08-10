package com.carenest.chat.data.remote

import com.carenest.chat.data.remote.dto.MessageDto
import com.carenest.chat.data.remote.dto.SendMessageRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

interface ChatRemoteDataSource {
    suspend fun getMessages(reservationId: String, after: String? = null): List<MessageDto>
    suspend fun sendMessage(reservationId: String, content: String): MessageDto
}

class KtorChatRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
) : ChatRemoteDataSource {

    override suspend fun getMessages(reservationId: String, after: String?): List<MessageDto> {
        val response = httpClient.get("/api/v1/reservations/$reservationId/messages") {
            if (!after.isNullOrBlank()) {
                parameter("after", after)
            }
        }
        return response.successBody()
    }

    override suspend fun sendMessage(reservationId: String, content: String): MessageDto {
        val response = httpClient.post("/api/v1/reservations/$reservationId/messages") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequestDto(content = content))
        }
        return response.successBody()
    }

    private suspend inline fun <reified T> HttpResponse.successBody(): T {
        if (!status.isSuccess()) {
            throw IllegalStateException("HTTP ${status.value}: ${status.description}")
        }
        return body()
    }
}
