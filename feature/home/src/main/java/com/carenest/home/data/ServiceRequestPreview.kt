package com.carenest.home.data


import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestPreviewDto(
    val serviceRequestId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String,
    val preferredDate: String,
    val preferredTime: PreferredTimeDto,
    val status: String,
    val estimatedPrice: Double,
    val createdAt: String,
    val patient: PatientPreviewDto
)

@Serializable
data class PreferredTimeDto(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nano: Int
)

@Serializable
data class PatientPreviewDto(
    val profileId: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String?,
    val dateOfBirth: String,
    val gender: String,
    val bloodType: String?,
    val height: Double,
    val weight: Double,
    val mobilityStatus: String?,
    val mobilityNotes: String?,
    val previousSurgeries: String?,
    val previousHospitalizations: String?,
    val allergies: List<String> = emptyList(),
    val medicalConditions: List<String> = emptyList(),
    val medications: List<String> = emptyList(),
    val medicalHistory: List<MedicalHistoryItemDto> = emptyList(),
    val emergencyContacts: List<EmergencyContactDto> = emptyList()
)

@Serializable
data class MedicalHistoryItemDto(
    val type: String,
    val description: String
)

@Serializable
data class EmergencyContactDto(
    val name: String,
    val relationship: String,
    val phoneNumber: String
)