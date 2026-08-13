package com.carenest.provider.core.network.socket.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ReservationEventDto(
    val type: String,
    val reservationId: String,
    val data: JsonElement? = null
)

@Serializable
data class ChatMessageResponseDto(
    val id: String,
    val serviceRequestId: String,
    val senderUserId: String,
    val senderName: String,
    val senderPhone: String,
    val content: String,
    val createdAt: String
)

@Serializable
data class NotificationResponseDto(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val relatedEntityType: String? = null,
    val relatedEntityId: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class SocketErrorPayloadDto(
    val code: String,
    val message: String,
    val timestamp: String
)

@Serializable
data class NurseOfferResponseDto(
    val id: String,
    val serviceRequestId: String,
    val nurse: NurseDto,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class NurseDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val ratingAvg: Double,
    val totalReviews: Int
)

@Serializable
data class NearbyNurseServiceRequestResponseDto(
    val serviceRequestId: String,
    val profileId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: String? = null,
    val status: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val estimatedPrice: Double? = null,
    val createdAt: String
)

@Serializable
data class AvailabilityRequestDto(
    val available: Boolean,
    val lat: Double? = null,
    val lng: Double? = null
)

@Serializable
data class LocationRequestDto(
    val lat: Double,
    val lng: Double
)

@Serializable
data class CreateOfferRequestDto(
    val serviceRequestId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class UpdateOfferRequestDto(
    val offerId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class AcceptOfferRequestDto(
    val offerId: String
)

@Serializable
data class WithdrawOfferRequestDto(
    val offerId: String
)

@Serializable
data class CancelReservationRequestDto(
    val serviceRequestId: String
)

@Serializable
data class ListOffersRequestDto(
    val serviceRequestId: String
)

@Serializable
data class SendChatMessageRequestDto(
    val content: String
)
