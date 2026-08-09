package com.carenest.chat.data.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.ChatParticipant
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class ChatDataSourceImp @Inject constructor(
    private val nurseSocketClient: NurseSocketClient,
) : ChatDataSource {

    private fun todayAt(hour: Int, minute: Int): Long =
        LocalDate.now()
            .atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        nurseSocketClient.connect()
        nurseSocketClient.subscribeToChat(requestId)

        return ChatSession(
            participant = ChatParticipant(
                participantId = "patient_$requestId",
                name = "Active Patient",
                photoUrl = null,
                isOnline = true,
                phoneNumber = "+13105550142",
            ),
            messages = listOf(
                ChatMessage(
                    id = "sys_$requestId",
                    type = ChatMessageType.SYSTEM_TIP,
                    text = "You are communicating with your patient. You can share visit updates or coordinates securely through this chat.",
                    senderType = MessageSender.NURSE,
                    sentAtEpochMillis = System.currentTimeMillis(),
                ),
            ),
        )
    }

    override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
        nurseSocketClient.connect()
        nurseSocketClient.sendChatMessage(requestId, text)

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