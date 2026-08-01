package com.carenest.chat.domain.usecase

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(requestId: String, text: String): Result<ChatMessage> =
        repository.sendMessage(requestId, text)
}