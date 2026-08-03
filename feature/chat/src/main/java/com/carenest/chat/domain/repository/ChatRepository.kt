package com.carenest.chat.domain.repository

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatSession


interface ChatRepository {
    suspend fun getChatSession(requestId: String): Result<ChatSession>
    suspend fun sendMessage(requestId: String, text: String): Result<ChatMessage>
}