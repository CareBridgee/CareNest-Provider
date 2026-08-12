package com.carenest.request.domain.model

data class PatientMedicalSummary(
    val profileId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String = "",
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
    val medicalHistory: List<MedicalHistoryItem> = emptyList(),
    val emergencyContacts: List<EmergencyContactItem> = emptyList(),
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

data class MedicalHistoryItem(
    val type: String = "",
    val description: String = "",
)

data class EmergencyContactItem(
    val name: String = "",
    val relationship: String = "",
    val phoneNumber: String = "",
)
