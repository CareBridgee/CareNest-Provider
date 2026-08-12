package com.carenest.request.data.mapper

import com.carenest.request.data.remote.dto.AddressSummaryDto
import com.carenest.request.data.remote.dto.NurseOfferDto
import com.carenest.request.data.remote.dto.PatientReportDto
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.data.remote.dto.ServiceRequestNursePreviewDto
import com.carenest.request.data.remote.dto.ServiceRequestNurseProfileDto
import com.carenest.request.data.remote.dto.PatientMedicalSummaryDto
import com.carenest.request.domain.model.EmergencyContactItem
import com.carenest.request.domain.model.MedicalHistoryItem
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientInfo
import com.carenest.request.domain.model.PatientMedicalSummary
import com.carenest.request.domain.model.VisitSummary
import java.util.Calendar
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

fun ServiceRequestDetailsDto.toDomainOffer(
    requestedServiceRequestId: String,
    acceptedOffer: NurseOfferDto?,
    assignedProfile: ServiceRequestNurseProfileDto?,
    patientReport: PatientReportDto?,
): Offer {
    val summary = profile
    val patient = assignedProfile?.patient
    val profileId = patient?.profileId ?: summary?.id.orEmpty()
    val patientName = listOf(
        patient?.firstName ?: summary?.firstName,
        patient?.lastName ?: summary?.lastName,
    ).joinNonBlank()
    val patientImageUrl = patient?.profileImageUrl
        ?.takeIf(String::isNotBlank)
        ?: summary?.profileImageUrl.orEmpty()
    val distanceMiles = (distanceKm ?: acceptedOffer?.distanceKm)?.toMiles()
    val estimatedDurationMinutes = serviceType?.estimatedDurationMinutes
        ?: acceptedOffer?.estimatedDurationMinutes

    return Offer(
        offerId = serviceRequestId ?: requestedServiceRequestId,
        nurseOfferId = acceptedOffer?.id,
        // The latest service-request contracts do not expose a reservation ID.
        reservationId = null,
        serviceRequestStatus = status ?: assignedProfile?.status,
        patientInfo = PatientInfo(
            id = profileId,
            name = patientName,
            image = patientImageUrl,
            distanceMiles = distanceMiles,
            age = patient?.dateOfBirth.toAgeOrNull(),
            phone = (assignedProfile?.patientPhoneNumber ?: summary?.phoneNumber).orEmpty(),
            addressLine = assignedProfile?.address.addressLine(),
            addressDetail = assignedProfile?.address.addressDetail(),
            summery = patientReport?.report
                ?: assignedProfile?.serviceDescription
                ?: serviceDescription.orEmpty(),
            latitude = latitude,
            longitude = longitude,
        ),
        visitDate = (
            acceptedOffer?.proposedDate
                ?: assignedProfile?.preferredDate
                ?: preferredDate
            ).orPlaceholder(),
        visitTime = (
            acceptedOffer?.proposedTime
                ?: assignedProfile?.preferredTime
                ?: preferredTime
            ).toDisplayTime().orPlaceholder(),
        distanceMiles = distanceMiles,
        estimatedArrival = PLACEHOLDER,
        estimatedDuration = estimatedDurationMinutes?.let { "$it mins" } ?: PLACEHOLDER,
        totalAmount = (
            acceptedOffer?.proposedPrice
                ?: assignedProfile?.estimatedPrice
                ?: serviceType?.basePrice
            )?.toFloat(),
        serviceType = (serviceType?.name ?: assignedProfile?.serviceName).orEmpty(),
        serviceImage = "",
    )
}

fun ServiceRequestNursePreviewDto.toDomainOffer(
    requestedServiceRequestId: String,
    details: ServiceRequestDetailsDto?,
    acceptedOffer: NurseOfferDto?,
    patientReport: PatientReportDto?,
): Offer {
    val profile = ServiceRequestNurseProfileDto(
        serviceRequestId = serviceRequestId,
        serviceTypeId = serviceTypeId,
        serviceName = serviceName,
        serviceDescription = serviceDescription,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        status = status,
        estimatedPrice = estimatedPrice,
        createdAt = createdAt,
        patient = patient,
    )
    return (details ?: profile.toDetailsDto()).toDomainOffer(
        requestedServiceRequestId = requestedServiceRequestId,
        acceptedOffer = acceptedOffer,
        assignedProfile = profile,
        patientReport = patientReport,
    )
}

fun ServiceRequestNurseProfileDto.toDomainOffer(
    requestedServiceRequestId: String,
    details: ServiceRequestDetailsDto?,
    acceptedOffer: NurseOfferDto?,
    patientReport: PatientReportDto?,
): Offer = (details ?: toDetailsDto()).toDomainOffer(
    requestedServiceRequestId = requestedServiceRequestId,
    acceptedOffer = acceptedOffer,
    assignedProfile = this,
    patientReport = patientReport,
)

private fun ServiceRequestNurseProfileDto.toDetailsDto(): ServiceRequestDetailsDto =
    ServiceRequestDetailsDto(
        serviceRequestId = serviceRequestId,
        serviceType = com.carenest.request.data.remote.dto.ServiceTypeSummaryDto(
            id = serviceTypeId,
            name = serviceName,
            basePrice = estimatedPrice,
        ),
        profile = com.carenest.request.data.remote.dto.ProfileSummaryDto(
            id = patient?.profileId,
            firstName = patient?.firstName,
            lastName = patient?.lastName,
            phoneNumber = patientPhoneNumber,
            profileImageUrl = patient?.profileImageUrl,
        ),
        serviceDescription = serviceDescription,
        preferredDate = preferredDate,
        preferredTime = preferredTime,
        status = status,
        createdAt = createdAt,
    )

fun ServiceRequestDetailsDto.toVisitSummary(
    requestedServiceRequestId: String,
    acceptedOffer: NurseOfferDto?,
): VisitSummary {
    check(status.equals("COMPLETED", ignoreCase = true)) {
        "The service request is not completed yet."
    }
    return VisitSummary(
        requestId = serviceRequestId ?: requestedServiceRequestId,
        professionalName = listOf(nurse?.firstName, nurse?.lastName)
            .joinNonBlank()
            .orPlaceholder(),
        patientName = listOf(profile?.firstName, profile?.lastName)
            .joinNonBlank()
            .takeIf { it.isNotBlank() },
        serviceType = serviceType?.name.orPlaceholder(),
        durationMinutes = durationMinutes,
        completedDate = updatedAt?.substringBefore('T').orPlaceholder(),
        totalAmount = acceptedOffer?.proposedPrice ?: serviceType?.basePrice,
        isVerified = true,
    )
}

private fun List<String?>.joinNonBlank(): String =
    mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }.joinToString(" ")

private fun List<String?>.joinNonBlankAddressParts(): String =
    mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }.joinToString(", ")

private fun AddressSummaryDto?.addressLine(): String = listOf(
    this?.buildingNumber,
    this?.street,
).joinNonBlank()

private fun AddressSummaryDto?.addressDetail(): String = listOf(
    this?.apartmentNumber,
    this?.area,
    this?.city,
    this?.country,
).joinNonBlankAddressParts()

private fun JsonElement?.toDisplayTime(): String = when (this) {
    is JsonPrimitive -> content.trim().removeSuffix(":00")
    is JsonObject -> {
        val hour = (get("hour") as? JsonPrimitive)?.intOrNull
        val minute = (get("minute") as? JsonPrimitive)?.intOrNull
        if (hour == null || minute == null) "" else "%02d:%02d".format(hour, minute)
    }
    else -> ""
}

private fun String?.toAgeOrNull(): Int? {
    val parts = this?.split('-') ?: return null
    if (parts.size < 3) return null
    val birthYear = parts[0].toIntOrNull() ?: return null
    val birthMonth = parts[1].toIntOrNull() ?: return null
    val birthDay = parts[2].take(2).toIntOrNull() ?: return null
    val now = Calendar.getInstance()
    var age = now.get(Calendar.YEAR) - birthYear
    val currentMonth = now.get(Calendar.MONTH) + 1
    val currentDay = now.get(Calendar.DAY_OF_MONTH)
    if (currentMonth < birthMonth || currentMonth == birthMonth && currentDay < birthDay) age--
    return age.coerceAtLeast(0)
}

private fun String?.orPlaceholder(): String = this?.takeIf(String::isNotBlank) ?: PLACEHOLDER

private fun Double.toMiles(): Float = (this * KILOMETERS_TO_MILES).toFloat()

fun PatientMedicalSummaryDto.toDomain(): PatientMedicalSummary = PatientMedicalSummary(
    profileId = profileId.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    profileImageUrl = profileImageUrl.orEmpty(),
    dateOfBirth = dateOfBirth,
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
    medicalHistory = medicalHistory.map {
        MedicalHistoryItem(
            type = it.type.orEmpty(),
            description = it.description.orEmpty(),
        )
    },
    emergencyContacts = emergencyContacts.map {
        EmergencyContactItem(
            name = it.name.orEmpty(),
            relationship = it.relationship.orEmpty(),
            phoneNumber = it.phoneNumber.orEmpty(),
        )
    },
)

private const val PLACEHOLDER = "\u2014"
private const val KILOMETERS_TO_MILES = 0.621371192237334
