package com.carenest.request.domain.model

enum class CancellationReason {
    VEHICLE_ISSUE,
    PERSONAL_EMERGENCY,
    LOCATION_INACCESSIBLE,
    SAFETY_CONCERN,
    INCORRECT_PATIENT_DETAILS,
    INAPPROPRIATE_CONDUCT,
    OTHER,
}

data class Offer(
    val offerId: String,
    val nurseOfferId: String? = null,
    val reservationId: String? = null,
    val serviceRequestStatus: String? = null,
    val patientInfo: PatientInfo,
    val visitDate: String,
    val visitTime: String,
    val distanceMiles: Float?,
    val estimatedArrival: String,
    val estimatedDuration: String,
    val totalAmount: Float?,
    val serviceType: String,
    val serviceImage : String,
)
