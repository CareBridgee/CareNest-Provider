package com.carenest.home.domain.model


import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ServiceRequestPreview(
    val serviceRequestId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String,
    val preferredDate: LocalDate,
    val preferredTime: LocalTime,
    val status: ServiceRequestStatus,
    val estimatedPrice: Double,
    val createdAt: LocalDateTime,
    val patient: PatientPreview
)

enum class ServiceRequestStatus {
    PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED, UNKNOWN;

    companion object {
        fun from(raw: String): ServiceRequestStatus =
            entries.find { it.name == raw } ?: UNKNOWN
    }
}

data class PatientPreview(
    val profileId: String,
    val fullName: String,
    val profileImageUrl: String?,
    val dateOfBirth: LocalDate,
    val gender: String,
    val bloodType: String?,
    val height: Double,
    val weight: Double,
    val mobilityStatus: String?,
    val mobilityNotes: String?,
    val previousSurgeries: String?,
    val previousHospitalizations: String?,
    val allergies: List<String>,
    val medicalConditions: List<String>,
    val medications: List<String>,
    val medicalHistory: List<MedicalHistoryItem>,
    val emergencyContacts: List<EmergencyContact>
)

data class MedicalHistoryItem(val type: String, val description: String)

data class EmergencyContact(val name: String, val relationship: String, val phoneNumber: String)