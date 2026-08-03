package com.carenest.chat.data.repository

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.repository.ChatRepository
import com.carenest.chat.data.datasource.ChatDataSource
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dataSource: ChatDataSource,
) : ChatRepository {

    override suspend fun getChatSession(requestId: String): Result<ChatSession> =
        runCatching { dataSource.fetchChatSession(requestId) }

    override suspend fun sendMessage(requestId: String, text: String): Result<ChatMessage> =
        runCatching { dataSource.sendMessage(requestId, text) }
}