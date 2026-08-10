package com.carenest.chat.data.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.chat.data.remote.ChatRemoteDataSource
import com.carenest.chat.data.remote.dto.MessageDto
import com.carenest.chat.domain.model.ChatMessage
import com.carenest.chat.domain.model.ChatMessageType
import com.carenest.chat.domain.model.ChatParticipant
import com.carenest.chat.domain.model.ChatSession
import com.carenest.chat.domain.model.MessageSender
import com.carenest.chat.domain.model.MessageStatus
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class ChatPatientProfileDto(
    val patient: ChatPatientSummaryDto? = null,
    val patientPhoneNumber: String? = null,
)

@Serializable
private data class ChatPatientSummaryDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
private data class ChatServiceDetailsDto(
    val profile: ChatProfileSummaryDto? = null,
)

@Serializable
private data class ChatProfileSummaryDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
)

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class ChatDataSourceImp @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource,
    private val nurseSocketClient: NurseSocketClient,
    private val httpClient: HttpClient,
    private val sessionStore: AuthenticationSessionStore,
) : ChatDataSource {

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        nurseSocketClient.connect()
        nurseSocketClient.subscribeToChat(requestId)

        val (patientName, photoUrl, phone) = try {
            val response = httpClient.get("/api/v1/service-requests/$requestId/profile")
            if (response.status.isSuccess()) {
                val dto = response.body<ChatPatientProfileDto>()
                val name = listOfNotNull(dto.patient?.firstName, dto.patient?.lastName)
                    .joinToString(" ").trim().takeIf { it.isNotEmpty() }
                Triple(name, dto.patient?.profileImageUrl, dto.patientPhoneNumber)
            } else {
                val detailsResponse = httpClient.get("/api/v1/service-requests/$requestId")
                if (detailsResponse.status.isSuccess()) {
                    val dto = detailsResponse.body<ChatServiceDetailsDto>()
                    val name = listOfNotNull(dto.profile?.firstName, dto.profile?.lastName)
                        .joinToString(" ").trim().takeIf { it.isNotEmpty() }
                    Triple(name, dto.profile?.profileImageUrl, dto.profile?.phoneNumber)
                } else {
                    Triple(null, null, null)
                }
            }
        } catch (_: Exception) {
            Triple(null, null, null)
        }

        val displayName = patientName ?: "Patient"

        val currentNurseUserId = sessionStore.state.firstOrNull()?.session?.nurseId
        val restMessages = try {
            remoteDataSource.getMessages(requestId).map { it.toDomain(currentNurseUserId) }
        } catch (_: Exception) {
            emptyList()
        }

        val systemTip = ChatMessage(
            id = "sys_$requestId",
            type = ChatMessageType.SYSTEM_TIP,
            text = "You are communicating with $displayName. You can share visit updates or coordinates securely through this chat.",
            senderType = MessageSender.NURSE,
            sentAtEpochMillis = System.currentTimeMillis() - 86400000L,
        )

        val allMessages = (listOf(systemTip) + restMessages)
            .distinctBy { it.id }
            .sortedBy { it.sentAtEpochMillis }

        return ChatSession(
            participant = ChatParticipant(
                participantId = "patient_$requestId",
                name = displayName,
                photoUrl = photoUrl,
                isOnline = true,
                phoneNumber = phone.orEmpty(),
            ),
            messages = allMessages,
        )
    }

    override suspend fun fetchChatMessages(requestId: String, after: String?): List<ChatMessage> {
        val currentNurseUserId = sessionStore.state.firstOrNull()?.session?.nurseId
        val dtoList = remoteDataSource.getMessages(requestId, after)
        return dtoList.map { it.toDomain(currentNurseUserId) }
    }

    override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
        nurseSocketClient.connect()
        val currentNurseUserId = sessionStore.state.firstOrNull()?.session?.nurseId

        val dto = remoteDataSource.sendMessage(requestId, text)

        runCatching {
            nurseSocketClient.sendChatMessage(requestId, text)
        }

        return dto.toDomain(currentNurseUserId)
    }

    private fun MessageDto.toDomain(currentNurseUserId: String?): ChatMessage {
        val isNurseSender = !currentNurseUserId.isNullOrBlank() &&
                (senderUserId.equals(currentNurseUserId, ignoreCase = true) ||
                        senderName?.contains("Nurse", ignoreCase = true) == true ||
                        senderUserId.contains("nurse", ignoreCase = true))

        val senderType = if (isNurseSender) MessageSender.NURSE else MessageSender.PATIENT
        val type = if (isNurseSender) ChatMessageType.OUTGOING else ChatMessageType.INCOMING

        return ChatMessage(
            id = id,
            type = type,
            text = content,
            senderType = senderType,
            sentAtEpochMillis = parseIsoToEpochMillis(createdAt),
            status = MessageStatus.SENT,
            createdAtIso = createdAt,
        )
    }

    private fun parseIsoToEpochMillis(isoString: String?): Long {
        if (isoString.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            Instant.parse(isoString).toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}