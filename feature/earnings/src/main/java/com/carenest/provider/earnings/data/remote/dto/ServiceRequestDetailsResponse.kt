package com.carenest.provider.earnings.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServiceTypeDetailsDto(
    val id: String? = null,
    val name: String? = null,
    val basePrice: Double? = null,
)

@Serializable
data class NurseOfferDetailsDto(
    val id: String? = null,
    val proposedPrice: Double? = null,
    val status: String? = null,
)

@Serializable
data class ServiceRequestDetailsResponse(
    val serviceRequestId: String? = null,
    val estimatedPrice: Double? = null,
    val serviceType: ServiceTypeDetailsDto? = null,
    val offers: List<NurseOfferDetailsDto> = emptyList(),
)
