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
    val distanceKm: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val offers: List<NurseOfferDto> = emptyList(),
)

@Serializable
data class NurseSummaryDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val ratingAvg: Double? = null,
    val totalReviews: Int? = null,
)

@Serializable
data class ServiceTypeSummaryDto(
    val id: String? = null,
    val name: String? = null,
    val basePrice: Double? = null,
    val estimatedDurationMinutes: Int? = null,
)

@Serializable
data class ProfileSummaryDto(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class NurseOfferDto(
    val id: String? = null,
    val serviceRequestId: String? = null,
    val nurse: NurseSummaryDto? = null,
    val proposedPrice: Double? = null,
    val proposedDate: String? = null,
    val proposedTime: JsonElement? = null,
    val message: String? = null,
    val status: String? = null,
    val distanceKm: Double? = null,
    val serviceTypeName: String? = null,
    val estimatedDurationMinutes: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ServiceRequestNursePreviewDto(
    val serviceRequestId: String? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: JsonElement? = null,
    val status: String? = null,
    val estimatedPrice: Double? = null,
    val createdAt: String? = null,
    val patient: PatientMedicalSummaryDto? = null,
)

@Serializable
data class ServiceRequestNurseProfileDto(
    val serviceRequestId: String? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: JsonElement? = null,
    val status: String? = null,
    val estimatedPrice: Double? = null,
    val createdAt: String? = null,
    val patient: PatientMedicalSummaryDto? = null,
    val patientPhoneNumber: String? = null,
    val address: AddressSummaryDto? = null,
)

@Serializable
data class PatientMedicalSummaryDto(
    val profileId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
    val allergies: List<String> = emptyList(),
    val medicalConditions: List<String> = emptyList(),
    val medications: List<String> = emptyList(),
    val medicalHistory: List<MedicalHistoryItemDto> = emptyList(),
    val emergencyContacts: List<EmergencyContactItemDto> = emptyList(),
)

@Serializable
data class MedicalHistoryItemDto(
    val type: String? = null,
    val description: String? = null,
)

@Serializable
data class EmergencyContactItemDto(
    val name: String? = null,
    val relationship: String? = null,
    val phoneNumber: String? = null,
)

@Serializable
data class PatientReportDto(
    val profileId: String? = null,
    val report: String? = null,
)

@Serializable
data class AddressSummaryDto(
    val country: String? = null,
    val city: String? = null,
    val area: String? = null,
    val street: String? = null,
    val buildingNumber: String? = null,
    val apartmentNumber: String? = null,
)

@Serializable
data class CompleteServiceRequestDto(
    val visitCode: String,
)
