package com.carenest.chat.domain.model

enum class ChatMessageType { INCOMING, OUTGOING, SYSTEM_TIP }

enum class MessageStatus { SENDING, SENT, DELIVERED, SEEN }
enum class MessageSender { NURSE, PATIENT }

data class ChatMessage(
    val id: String,
    val type: ChatMessageType,
    val text: String,
    val senderType : MessageSender,
    val sentAtEpochMillis: Long,
    val status: MessageStatus = MessageStatus.SENT,
)

data class ChatParticipant(
    val participantId: String,
    val name: String,
    val photoUrl: String?,
    val isOnline: Boolean,
    val phoneNumber: String,
)

data class ChatSession(
    val participant: ChatParticipant,
    val messages: List<ChatMessage>,
)