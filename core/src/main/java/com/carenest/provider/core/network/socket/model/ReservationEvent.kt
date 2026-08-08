package com.carenest.provider.core.network.socket.model

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
    val type: String,
    val reservationId: String? = null,
    val data: JsonElement? = null
) {
    val eventType: ReservationEventType
        get() = try {
            ReservationEventType.valueOf(type)
        } catch (e: Exception) {
            ReservationEventType.UNKNOWN
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
        } catch (e: Exception) {
            null
        }
    }
}
