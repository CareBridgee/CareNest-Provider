package com.carenest.chat.data.datasource


import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.ChatParticipant
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class ChatDataSourceImp @Inject constructor() : ChatDataSource {

    private fun todayAt(hour: Int, minute: Int): Long =
        LocalDate.now()
            .atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        delay(500)
        return ChatSession(
            participant = ChatParticipant(
                participantId = "patient_001",
                name = "Elena Vance",
                photoUrl = null,
                isOnline = true,
                phoneNumber = "+13105550142",
            ),
            messages = listOf(
                ChatMessage(
                    id = "sys_1",
                    type = ChatMessageType.SYSTEM_TIP,
                    text = "You are communicating with Elena. You can share visit updates or coordinates securely through this encrypted chat.",
                    senderType = MessageSender.NURSE,
                    sentAtEpochMillis = todayAt(10, 41),
                ),
                ChatMessage(
                    id = "msg_1",
                    type = ChatMessageType.OUTGOING,
                    text = "Hello Elena! I'm on my way to your location. I should be there in about 10 minutes.",
                    senderType = MessageSender.NURSE,
                    sentAtEpochMillis = todayAt(10, 42),
                ),
                ChatMessage(
                    id = "msg_2",
                    type = ChatMessageType.INCOMING,
                    text = "Thank you, Sarah. I have the medical reports ready for you.",
                    senderType = MessageSender.PATIENT,
                    sentAtEpochMillis = todayAt(10, 43),
                    status = MessageStatus.SEEN,
                ),
            ),
        )
    }

    override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
        delay(300)
        return ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            type = ChatMessageType.OUTGOING,
            text = text,
            senderType = MessageSender.NURSE,
            sentAtEpochMillis = System.currentTimeMillis(),
            status = MessageStatus.SENT,
        )
    }
}