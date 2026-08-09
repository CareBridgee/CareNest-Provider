package com.carenest.chat.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import com.carenest.chat.domain.usecase.GetChatSessionUseCase
import com.carenest.chat.domain.usecase.SendMessageUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatSessionUseCase: GetChatSessionUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val nurseSocketClient: NurseSocketClient,
) : ViewModel(), EffectPublisher<ChatEffect> by DefaultEffectPublisher(),
    StateHolder<ChatState> by DefaultStateHolder(ChatState()) {

    private var requestId: String? = null
    private var socketMessagesJob: Job? = null

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
        nurseSocketClient.connect()
        observeSocketMessages(requestId)

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

    private fun observeSocketMessages(requestId: String) {
        socketMessagesJob?.cancel()
        socketMessagesJob = viewModelScope.launch {
            nurseSocketClient.chatMessages.collect { socketMsg ->
                if (socketMsg.serviceRequestId == requestId || socketMsg.serviceRequestId.isEmpty()) {
                    val isNurseSender = socketMsg.senderName?.contains("Nurse", ignoreCase = true) == true ||
                            socketMsg.senderUserId.contains("nurse", ignoreCase = true)
                    val newMsg = ChatMessage(
                        id = socketMsg.id,
                        type = if (isNurseSender) ChatMessageType.OUTGOING else ChatMessageType.INCOMING,
                        text = socketMsg.content,
                        senderType = if (isNurseSender) MessageSender.NURSE else MessageSender.PATIENT,
                        sentAtEpochMillis = System.currentTimeMillis(),
                        status = MessageStatus.DELIVERED
                    )
                    updateState {
                        if (messages.any { it.id == newMsg.id || (it.text == newMsg.text && it.senderType == newMsg.senderType) }) {
                            this
                        } else {
                            copy(messages = messages + newMsg)
                        }
                    }
                    sendEffect(ChatEffect.ScrollToBottom)
                }
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
                        if (messages.any { it.id == message.id }) {
                            copy(isSending = false)
                        } else {
                            copy(isSending = false, messages = messages + message)
                        }
                    }
                    sendEffect(ChatEffect.ScrollToBottom)
                }
                .onFailure { throwable ->
                    updateState { copy(isSending = false) }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    override fun onCleared() {
        socketMessagesJob?.cancel()
        requestId?.let { id ->
            viewModelScope.launch {
                nurseSocketClient.unsubscribeFromChat(id)
            }
        }
        super.onCleared()
    }
}