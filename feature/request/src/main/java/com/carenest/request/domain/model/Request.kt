package com.carenest.request.domain.model

data class Request(
    val id: String,
    val patientName: String,
    val patientImage: String = "",
    val patientAddress: String,
    val basePrice: Float,
    val serviceName: String,
    val serviceImage: String,
    val status: RequestStatus
)

enum class RequestStatus {
    ESTIMATED, CANCELED,ACCEPTED
}
