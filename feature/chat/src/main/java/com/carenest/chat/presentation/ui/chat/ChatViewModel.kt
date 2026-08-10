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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
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
            is ChatIntent.OnRetrySendMessageClicked -> retrySendMessage(intent.message)
            ChatIntent.OnCallClicked -> {
                val phone = currentState.participant?.phoneNumber ?: return
                sendEffect(ChatEffect.InitiateCall(phone))
            }
            ChatIntent.OnBackClicked -> sendEffect(ChatEffect.NavigateBack)
            ChatIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun loadChat(requestId: String) {
        if (this.requestId == requestId && currentState.messages.isNotEmpty()) {
            return
        }
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
                    sendEffect(ChatEffect.ScrollToBottom)
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
            val currentNurseId = sessionStore.state.firstOrNull()?.session?.nurseId
            nurseSocketClient.chatMessages.collect { socketMsg ->
                if (socketMsg.serviceRequestId == requestId || socketMsg.serviceRequestId.isEmpty()) {
                    val trimmedContent = socketMsg.content.trim()

                    val matchesExistingNurseMessage = currentState.messages.any {
                        it.senderType == MessageSender.NURSE && it.text.trim() == trimmedContent
                    }

                    val isNurseSender = matchesExistingNurseMessage ||
                            (!currentNurseId.isNullOrBlank() && socketMsg.senderUserId.equals(currentNurseId, ignoreCase = true)) ||
                            (socketMsg.senderName?.contains("Nurse", ignoreCase = true) == true) ||
                            (socketMsg.senderUserId.contains("nurse", ignoreCase = true))

                    val senderType = if (isNurseSender) MessageSender.NURSE else MessageSender.PATIENT
                    val messageType = if (isNurseSender) ChatMessageType.OUTGOING else ChatMessageType.INCOMING

                    val socketEpochMillis = parseIsoToEpochMillis(socketMsg.createdAt)

                    val newMsg = ChatMessage(
                        id = socketMsg.id,
                        type = messageType,
                        text = socketMsg.content,
                        senderType = senderType,
                        sentAtEpochMillis = socketEpochMillis,
                        status = MessageStatus.DELIVERED,
                        createdAtIso = socketMsg.createdAt,
                    )

                    updateState {
                        val isDuplicateById = messages.any { it.id == newMsg.id }
                        val pendingNurseIndex = messages.indexOfFirst {
                            it.senderType == MessageSender.NURSE &&
                                    it.text.trim() == trimmedContent &&
                                    (it.id.startsWith("temp_") || it.status == MessageStatus.SENDING)
                        }

                        val updatedList = when {
                            isDuplicateById -> messages
                            pendingNurseIndex != -1 -> {
                                messages.toMutableList().apply {
                                    set(pendingNurseIndex, newMsg)
                                }
                            }
                            else -> messages + newMsg
                        }

                        copy(messages = updatedList.distinctBy { it.id }.sortedBy { it.sentAtEpochMillis })
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

        val tempId = "temp_${UUID.randomUUID()}"
        val tempMsg = ChatMessage(
            id = tempId,
            type = ChatMessageType.OUTGOING,
            text = text,
            senderType = MessageSender.NURSE,
            sentAtEpochMillis = System.currentTimeMillis(),
            status = MessageStatus.SENDING,
        )

        updateState {
            copy(
                inputText = "",
                messages = (messages + tempMsg).sortedBy { it.sentAtEpochMillis },
            )
        }
        sendEffect(ChatEffect.ScrollToBottom)

        performSendMessage(id, tempId, text)
    }

    private fun retrySendMessage(failedMsg: ChatMessage) {
        val id = requestId ?: return
        val tempId = failedMsg.id

        updateState {
            val updated = messages.map { msg ->
                if (msg.id == tempId) msg.copy(status = MessageStatus.SENDING) else msg
            }
            copy(messages = updated)
        }

        performSendMessage(id, tempId, failedMsg.text)
    }

    private fun performSendMessage(requestId: String, tempId: String, text: String) {
        viewModelScope.launch {
            sendMessageUseCase(requestId, text)
                .onSuccess { serverMsg ->
                    updateState {
                        val updated = messages.map { existing ->
                            if (existing.id == tempId || (existing.id.startsWith("temp_") && existing.text.trim() == text.trim())) {
                                serverMsg.copy(status = MessageStatus.SENT)
                            } else {
                                existing
                            }
                        }.distinctBy { it.id }.sortedBy { it.sentAtEpochMillis }

                        copy(messages = updated)
                    }
                    sendEffect(ChatEffect.ScrollToBottom)
                }
                .onFailure { throwable ->
                    updateState {
                        val updated = messages.map { existing ->
                            if (existing.id == tempId) {
                                existing.copy(status = MessageStatus.FAILED)
                            } else {
                                existing
                            }
                        }
                        copy(messages = updated)
                    }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    private fun parseIsoToEpochMillis(isoString: String?): Long {
        if (isoString.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            Instant.parse(isoString).toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
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