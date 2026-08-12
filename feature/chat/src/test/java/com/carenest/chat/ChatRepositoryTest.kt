package com.carenest.chat

import com.carenest.chat.data.datasource.ChatDataSource
import com.carenest.chat.data.repository.ChatRepositoryImpl
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.ChatParticipant
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryTest {

    private class FakeChatDataSource : ChatDataSource {
        val messagesStore = mutableListOf<ChatMessage>()

        override suspend fun fetchChatSession(requestId: String): ChatSession {
            val systemTip = ChatMessage(
                id = "sys_$requestId",
                type = ChatMessageType.SYSTEM_TIP,
                text = "System Tip",
                senderType = MessageSender.NURSE,
                sentAtEpochMillis = 1000L,
            )
            return ChatSession(
                participant = ChatParticipant("p1", "Patient Name", null, true, "123456"),
                messages = listOf(systemTip) + messagesStore,
            )
        }

        override suspend fun fetchChatMessages(requestId: String, after: String?): List<ChatMessage> {
            return messagesStore
        }

        override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
            val msg = ChatMessage(
                id = "server_uuid_100",
                type = ChatMessageType.OUTGOING,
                text = text,
                senderType = MessageSender.NURSE,
                sentAtEpochMillis = 2000L,
                status = MessageStatus.SENT,
            )
            messagesStore.add(msg)
            return msg
        }
    }

    @Test
    fun `getChatSession caches messages and returns deduplicated list`() = runTest {
        val dataSource = FakeChatDataSource()
        val repository = ChatRepositoryImpl(dataSource)

        val result = repository.getChatSession("req-123")

        assertTrue(result.isSuccess)
        val session = result.getOrNull()
        assertEquals(1, session?.messages?.size)
        assertEquals("sys_req-123", session?.messages?.first()?.id)
    }

    @Test
    fun `sendMessage replaces optimistic temporary message with server response`() = runTest {
        val dataSource = FakeChatDataSource()
        val repository = ChatRepositoryImpl(dataSource)

        // Initialize session
        repository.getChatSession("req-123")

        val sendResult = repository.sendMessage("req-123", "Hello patient")

        assertTrue(sendResult.isSuccess)
        val sentMsg = sendResult.getOrNull()
        assertEquals("server_uuid_100", sentMsg?.id)
        assertEquals("Hello patient", sentMsg?.text)

        // Verify cached messages contain server_uuid_100
        val historyResult = repository.getChatMessages("req-123")
        assertTrue(historyResult.isSuccess)
        val cached = historyResult.getOrNull().orEmpty()
        assertTrue(cached.any { it.id == "server_uuid_100" })
    }

    @Test
    fun `isNurseSender correctly identifies nurse vs patient messages`() {
        val isNurseByUserId = com.carenest.chat.data.datasource.isNurseSender(
            senderUserId = "nurse_123",
            senderName = "John Doe",
            senderPhone = "12345",
            currentNurseUserId = "nurse_123",
            currentNursePhone = null,
        )
        assertTrue(isNurseByUserId)

        val isNurseByPhone = com.carenest.chat.data.datasource.isNurseSender(
            senderUserId = "usr_abc",
            senderName = "John Doe",
            senderPhone = "+1234567890",
            currentNurseUserId = null,
            currentNursePhone = "+1 (234) 567-890",
        )
        assertTrue(isNurseByPhone)

        val isNurseByName = com.carenest.chat.data.datasource.isNurseSender(
            senderUserId = "usr_xyz",
            senderName = "Nurse Elena",
            senderPhone = null,
            currentNurseUserId = null,
            currentNursePhone = null,
        )
        assertTrue(isNurseByName)

        val isPatient = com.carenest.chat.data.datasource.isNurseSender(
            senderUserId = "patient_001",
            senderName = "Elena Patient",
            senderPhone = "+999999999",
            currentNurseUserId = "nurse_123",
            currentNursePhone = "+1234567890",
        )
        org.junit.Assert.assertFalse(isPatient)
    }
}
