package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityRequest(
    val available: Boolean,
    val lat: Double? = null,
    val lng: Double? = null
)

@Serializable
data class LocationUpdateRequest(
    val lat: Double,
    val lng: Double
)

@Serializable
data class CreateOfferRequest(
    val serviceRequestId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class UpdateOfferRequest(
    val offerId: String,
    val proposedPrice: Double? = null,
    val proposedDate: String? = null,
    val proposedTime: String? = null,
    val message: String? = null
)

@Serializable
data class AcceptOfferRequest(
    val offerId: String
)

@Serializable
data class WithdrawOfferRequest(
    val offerId: String
)

@Serializable
data class CancelReservationRequest(
    val serviceRequestId: String
)

@Serializable
data class ListOffersRequest(
    val serviceRequestId: String
)

@Serializable
data class SendChatMessageRequest(
    val content: String
)
