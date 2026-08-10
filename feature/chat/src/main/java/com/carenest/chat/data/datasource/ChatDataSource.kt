package com.carenest.chat.data.datasource

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatSession


interface ChatDataSource {
    suspend fun fetchChatSession(requestId: String): ChatSession
    suspend fun fetchChatMessages(requestId: String, after: String? = null): List<ChatMessage>
    suspend fun sendMessage(requestId: String, text: String): ChatMessage
}