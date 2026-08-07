package com.carenest.request.data.mapper

import com.carenest.request.data.remote.dto.NurseOfferDto
import com.carenest.request.data.remote.dto.PatientAddressDto
import com.carenest.request.data.remote.dto.PatientProfileDto
import com.carenest.request.data.remote.dto.PatientReportDto
import com.carenest.request.data.remote.dto.ServiceRequestDetailsDto
import com.carenest.request.data.remote.dto.VisitCodeDto
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientInfo
import com.carenest.request.domain.model.VisitCode
import com.carenest.request.domain.model.VisitSummary
import java.util.Calendar
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

fun ServiceRequestDetailsDto.toDomainOffer(
    requestedServiceRequestId: String,
    acceptedOffer: NurseOfferDto?,
    patientProfile: PatientProfileDto?,
    patientReport: PatientReportDto?,
    patientAddress: PatientAddressDto?,
): Offer {
    val summary = profile
    val profileId = summary?.id.orEmpty()
    val patientName = listOf(
        patientProfile?.firstName ?: summary?.firstName,
        patientProfile?.lastName ?: summary?.lastName,
    ).joinNonBlank()

    return Offer(
        offerId = serviceRequestId ?: requestedServiceRequestId,
        nurseOfferId = acceptedOffer?.id,
        reservationId = reservationId,
        serviceRequestStatus = status,
        patientInfo = PatientInfo(
            id = profileId,
            name = patientName,
            image = "",
            distanceMiles = null,
            age = patientProfile?.dateOfBirth.toAgeOrNull(),
            phone = summary?.phoneNumber.orEmpty(),
            addressLine = patientAddress.addressLine(),
            addressDetail = patientAddress.addressDetail(),
            summery = patientReport?.report ?: serviceDescription.orEmpty(),
        ),
        visitDate = (acceptedOffer?.proposedDate ?: preferredDate).orPlaceholder(),
        visitTime = (acceptedOffer?.proposedTime ?: preferredTime).toDisplayTime().orPlaceholder(),
        distanceMiles = null,
        estimatedArrival = PLACEHOLDER,
        estimatedDuration = durationMinutes?.let { "$it mins" } ?: PLACEHOLDER,
        totalAmount = (acceptedOffer?.proposedPrice ?: serviceType?.basePrice)?.toFloat(),
        serviceType = serviceType?.name.orEmpty(),
        serviceImage = "",
    )
}

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
        serviceType = serviceType?.name.orPlaceholder(),
        durationMinutes = durationMinutes,
        completedDate = updatedAt?.substringBefore('T').orPlaceholder(),
        totalAmount = acceptedOffer?.proposedPrice ?: serviceType?.basePrice,
        isVerified = true,
    )
}

fun VisitCodeDto.toDomain(fallbackServiceRequestId: String): VisitCode = VisitCode(
    serviceRequestId = serviceRequestId ?: fallbackServiceRequestId,
    code = code.orEmpty(),
    expiresAt = expiresAt,
)

private fun List<String?>.joinNonBlank(): String =
    mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }.joinToString(" ")

private fun List<String?>.joinNonBlankAddressParts(): String =
    mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }.joinToString(", ")

private fun PatientAddressDto?.addressLine(): String = listOf(
    this?.buildingNumber,
    this?.street,
).joinNonBlank()

private fun PatientAddressDto?.addressDetail(): String = listOf(
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

private const val PLACEHOLDER = "—"
