package com.carenest.chat.data.repository

import com.carenest.chat.data.datasource.ChatDataSource
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.repository.ChatRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val dataSource: ChatDataSource,
) : ChatRepository {

    private val messagesCache = ConcurrentHashMap<String, MutableList<ChatMessage>>()

    override suspend fun getChatSession(requestId: String): Result<ChatSession> = runCatching {
        val session = dataSource.fetchChatSession(requestId)
        val existing = messagesCache.getOrPut(requestId) { mutableListOf() }

        val merged = (existing + session.messages)
            .distinctBy { it.id }
            .sortedBy { it.sentAtEpochMillis }
            .toMutableList()

        messagesCache[requestId] = merged
        session.copy(messages = merged)
    }

    override suspend fun getChatMessages(requestId: String, after: String?): Result<List<ChatMessage>> = runCatching {
        val newMessages = dataSource.fetchChatMessages(requestId, after)
        val existing = messagesCache.getOrPut(requestId) { mutableListOf() }

        val merged = (existing + newMessages)
            .distinctBy { it.id }
            .sortedBy { it.sentAtEpochMillis }
            .toMutableList()

        messagesCache[requestId] = merged
        merged
    }

    override suspend fun sendMessage(requestId: String, text: String): Result<ChatMessage> = runCatching {
        val sentMessage = dataSource.sendMessage(requestId, text)
        val existing = messagesCache.getOrPut(requestId) { mutableListOf() }
        existing.removeAll { it.id == sentMessage.id || (it.text.trim() == sentMessage.text.trim() && it.id.startsWith("temp_")) }
        existing.add(sentMessage)
        existing.sortBy { it.sentAtEpochMillis }
        sentMessage
    }
}