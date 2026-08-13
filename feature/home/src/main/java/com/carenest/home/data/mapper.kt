package com.carenest.home.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.home.domain.model.EmergencyContact
import com.carenest.home.domain.model.MedicalHistoryItem
import com.carenest.home.domain.model.PatientPreview
import com.carenest.home.domain.model.ServiceRequestPreview
import com.carenest.home.domain.model.ServiceRequestStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
fun ServiceRequestPreviewDto.toDomain(): ServiceRequestPreview = ServiceRequestPreview(
    serviceRequestId = serviceRequestId,
    serviceTypeId = serviceTypeId,
    serviceName = serviceName,
    serviceDescription = serviceDescription,
    preferredDate = LocalDate.parse(preferredDate),
    preferredTime = preferredTime.toDomain(),
    status = ServiceRequestStatus.from(status),
    estimatedPrice = estimatedPrice,
    createdAt = LocalDateTime.parse(createdAt.removeSuffix("Z")),
    patient = patient.toDomain()
)

private fun PreferredTimeDto.toDomain(): LocalTime =
    LocalTime.of(hour, minute, second)

@RequiresApi(Build.VERSION_CODES.O)
private fun PatientPreviewDto.toDomain(): PatientPreview = PatientPreview(
    profileId = profileId,
    fullName = "$firstName $lastName".trim(),
    profileImageUrl = profileImageUrl,
    dateOfBirth = LocalDate.parse(dateOfBirth),
    gender = gender,
    bloodType = bloodType,
    height = height,
    weight = weight,
    mobilityStatus = mobilityStatus,
    mobilityNotes = mobilityNotes,
    previousSurgeries = previousSurgeries,
    previousHospitalizations = previousHospitalizations,
    allergies = allergies,
    medicalConditions = medicalConditions,
    medications = medications,
    medicalHistory = medicalHistory.map { MedicalHistoryItem(it.type, it.description) },
    emergencyContacts = emergencyContacts.map {
        EmergencyContact(
            it.name,
            it.relationship,
            it.phoneNumber
        )
    }
)