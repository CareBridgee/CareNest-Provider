package com.carenest.chat.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import com.carenest.chat.domain.usecase.GetChatSessionUseCase
import com.carenest.chat.domain.usecase.SendMessageUseCase
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatSessionUseCase: GetChatSessionUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val nurseSocketClient: NurseSocketClient,
    private val sessionStore: AuthenticationSessionStore,
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
            val currentNurseId = sessionStore.state.first().session?.nurseId
            nurseSocketClient.chatMessages.collect { socketMsg ->
                if (socketMsg.serviceRequestId == requestId || socketMsg.serviceRequestId.isEmpty()) {
                    val trimmedContent = socketMsg.content.trim()

                    // Check if any message in state was already sent by nurse with matching text
                    val matchesExistingNurseMessage = currentState.messages.any {
                        it.senderType == MessageSender.NURSE && it.text.trim() == trimmedContent
                    }

                    val isNurseSender = matchesExistingNurseMessage ||
                        (!currentNurseId.isNullOrBlank() && socketMsg.senderUserId.equals(currentNurseId, ignoreCase = true)) ||
                        (socketMsg.senderName?.contains("Nurse", ignoreCase = true) == true) ||
                        (socketMsg.senderUserId.contains("nurse", ignoreCase = true))

                    val senderType = if (isNurseSender) MessageSender.NURSE else MessageSender.PATIENT
                    val messageType = if (isNurseSender) ChatMessageType.OUTGOING else ChatMessageType.INCOMING

                    val newMsg = ChatMessage(
                        id = socketMsg.id,
                        type = messageType,
                        text = socketMsg.content,
                        senderType = senderType,
                        sentAtEpochMillis = System.currentTimeMillis(),
                        status = MessageStatus.DELIVERED
                    )

                    updateState {
                        val isDuplicate = messages.any { existing ->
                            existing.id == newMsg.id ||
                                    (existing.text.trim() == trimmedContent && (
                                            existing.senderType == newMsg.senderType ||
                                                    (existing.senderType == MessageSender.NURSE && newMsg.senderType == MessageSender.NURSE)
                                            ))
                        }

                        if (isDuplicate) {
                            // Update existing temporary local message ID with real socket message ID
                            val updatedMessages = messages.map { existing ->
                                if (existing.text.trim() == trimmedContent && existing.senderType == MessageSender.NURSE) {
                                    newMsg
                                } else {
                                    existing
                                }
                            }
                            copy(messages = updatedMessages)
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
                        val isAlreadyPresent = messages.any {
                            it.id == message.id || (it.text.trim() == message.text.trim() && it.senderType == MessageSender.NURSE)
                        }
                        if (isAlreadyPresent) {
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