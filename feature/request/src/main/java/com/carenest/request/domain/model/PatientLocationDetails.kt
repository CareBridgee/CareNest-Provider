package com.carenest.request.domain.model

data class PatientLocationDetails(
    val address: String,
    val apartment: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
)
