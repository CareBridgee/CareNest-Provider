package com.carenest.chat.domain.usecase

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(requestId: String, after: String? = null): Result<List<ChatMessage>> =
        repository.getChatMessages(requestId, after)
}
