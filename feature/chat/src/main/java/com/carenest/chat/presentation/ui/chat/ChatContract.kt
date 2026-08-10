package com.carenest.chat.presentation.ui.chat

import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatParticipant

data class ChatState(
    val isLoading: Boolean = true,
    val participant: ChatParticipant? = null,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ChatIntent {
    data class LoadChat(val requestId: String) : ChatIntent
    data class OnMessageInputChanged(val text: String) : ChatIntent
    data object OnSendMessageClicked : ChatIntent
    data class OnRetrySendMessageClicked(val message: ChatMessage) : ChatIntent
    data object OnCallClicked : ChatIntent
    data object OnBackClicked : ChatIntent
    data object OnErrorDismissed : ChatIntent
}

sealed interface ChatEffect {
    data class InitiateCall(val phoneNumber: String) : ChatEffect
    data object NavigateBack : ChatEffect
    data object ScrollToBottom : ChatEffect
    data class ShowError(val message: String) : ChatEffect
}