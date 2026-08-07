package com.carenest.request.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ServiceRequestDetailsDto(
    val serviceRequestId: String? = null,
    val serviceType: ServiceTypeSummaryDto? = null,
    val profile: ProfileSummaryDto? = null,
    val nurse: NurseSummaryDto? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: JsonElement? = null,
    val durationMinutes: Int? = null,
    val status: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val updatedAt: String? = null,
    val offers: List<NurseOfferDto> = emptyList(),
    val reservationId: String? = null,
)

@Serializable
data class NurseSummaryDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val ratingAvg: Double? = null,
    val totalReviews: Int? = null,
)

@Serializable
data class ServiceTypeSummaryDto(
    val id: String? = null,
    val name: String? = null,
    val basePrice: Double? = null,
)

@Serializable
data class ProfileSummaryDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
)

@Serializable
data class NurseOfferDto(
    val id: String? = null,
    val serviceRequestId: String? = null,
    val proposedPrice: Double? = null,
    val proposedDate: String? = null,
    val proposedTime: JsonElement? = null,
    val message: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class PatientProfileDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
)

@Serializable
data class PatientReportDto(
    val profileId: String? = null,
    val report: String? = null,
)

@Serializable
data class PatientAddressDto(
    val id: String? = null,
    val profileId: String? = null,
    val country: String? = null,
    val city: String? = null,
    val area: String? = null,
    val street: String? = null,
    val buildingNumber: String? = null,
    val apartmentNumber: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class VisitCodeDto(
    val serviceRequestId: String? = null,
    val code: String? = null,
    val expiresAt: String? = null,
)

@Serializable
data class CompleteServiceRequestDto(
    val visitCode: String,
)
