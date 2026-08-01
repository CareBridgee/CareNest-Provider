package com.carenest.chat.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.chat.domain.usecase.GetChatSessionUseCase
import com.carenest.chat.domain.usecase.SendMessageUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatSessionUseCase: GetChatSessionUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel(), EffectPublisher<ChatEffect> by DefaultEffectPublisher(),
    StateHolder<ChatState> by DefaultStateHolder(ChatState()) {

    private var requestId: String? = null

    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadChat -> loadChat(intent.requestId)
            is ChatIntent.OnMessageInputChanged -> updateState { copy(inputText = intent.text) }
            ChatIntent.OnSendMessageClicked -> sendMessage()
            ChatIntent.OnCallClicked -> {
                val phone = currentState.participant?.phoneNumber ?: return
                sendEffect(ChatEffect.InitiateCall(phone))
            }
            ChatIntent.OnBackClicked -> sendEffect(ChatEffect.NavigateBack)
            ChatIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun loadChat(requestId: String) {
        this.requestId = requestId
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            getChatSessionUseCase(requestId)
                .onSuccess { session ->
                    updateState {
                        copy(
                            isLoading = false,
                            participant = session.participant,
                            messages = session.messages,
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false, errorMessage = throwable.message) }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    private fun sendMessage() {
        val id = requestId ?: return
        val text = currentState.inputText.trim()
        if (text.isEmpty() || currentState.isSending) return

        viewModelScope.launch {
            updateState { copy(isSending = true, inputText = "") }

            sendMessageUseCase(id, text)
                .onSuccess { message ->
                    updateState {
                        copy(isSending = false, messages = messages + message)
                    }
                    sendEffect(ChatEffect.ScrollToBottom)
                }
                .onFailure { throwable ->
                    updateState { copy(isSending = false) }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }
}