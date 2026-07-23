package com.carenest.home.domain.model

enum class RequestStatus {
    ESTIMATED,
    CANCELED,
    ACCEPTED,
}

data class NurseRequest(
    val id: String,
    val patientName: String,
    val patientImage: String,
    val serviceType: String,
    val serviceImage : String,
    val baseRate: Float,
    val distanceMiles: Float,
    val status: RequestStatus = RequestStatus.ESTIMATED,
    val progressStep: Int = 1,
)