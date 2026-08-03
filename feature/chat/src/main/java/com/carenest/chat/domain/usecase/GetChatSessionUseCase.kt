package com.carenest.chat.domain.usecase

import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatSessionUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(requestId: String): Result<ChatSession> =
        repository.getChatSession(requestId)
}