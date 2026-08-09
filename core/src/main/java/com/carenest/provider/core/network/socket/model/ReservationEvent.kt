package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

enum class ReservationEventType {
    OFFER_CREATED,
    OFFER_UPDATED,
    OFFER_COUNTERED,
    OFFER_ACCEPTED,
    OFFER_WITHDRAWN,
    OFFER_REJECTED,
    REQUEST_CANCELLED,
    COMPLETED,
    OFFERS_LIST,
    UNKNOWN
}

@Serializable
data class ReservationEvent(
    val type: String? = null,
    @SerialName("eventType") val eventTypeStr: String? = null,
    val reservationId: String? = null,
    val serviceRequestId: String? = null,
    val requestId: String? = null,
    val data: JsonElement? = null
) {
    val effectiveReservationId: String?
        get() = reservationId ?: serviceRequestId ?: requestId ?: extractOfferId()

    val eventType: ReservationEventType
        get() {
            val rawType = (type ?: eventTypeStr ?: "").trim().uppercase()
            return try {
                ReservationEventType.valueOf(rawType)
            } catch (e: Exception) {
                when (rawType) {
                    "ACCEPT", "ACCEPTED", "OFFER_ACCEPTED" -> ReservationEventType.OFFER_ACCEPTED
                    "REJECT", "REJECTED", "OFFER_REJECTED" -> ReservationEventType.OFFER_REJECTED
                    "CANCEL", "CANCELLED", "CANCELED", "REQUEST_CANCELLED" -> ReservationEventType.REQUEST_CANCELLED
                    "COUNTER", "COUNTERED", "OFFER_COUNTERED" -> ReservationEventType.OFFER_COUNTERED
                    "CREATE", "CREATED", "OFFER_CREATED" -> ReservationEventType.OFFER_CREATED
                    "UPDATE", "UPDATED", "OFFER_UPDATED" -> ReservationEventType.OFFER_UPDATED
                    "WITHDRAW", "WITHDRAWN", "OFFER_WITHDRAWN" -> ReservationEventType.OFFER_WITHDRAWN
                    else -> ReservationEventType.UNKNOWN
                }
            }
        }

    fun asOfferResponse(json: Json = Json { ignoreUnknownKeys = true }): NurseOfferResponse? {
        val element = data ?: return null
        return try {
            json.decodeFromJsonElement<NurseOfferResponse>(element)
        } catch (e: Exception) {
            null
        }
    }

    fun asOffersList(json: Json = Json { ignoreUnknownKeys = true }): List<NurseOfferResponse> {
        val element = data ?: return emptyList()
        return try {
            json.decodeFromJsonElement<List<NurseOfferResponse>>(element)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun extractOfferId(): String? {
        val element = data ?: return null
        return try {
            element.jsonObject["offerId"]?.jsonPrimitive?.content
                ?: element.jsonObject["id"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }
}
