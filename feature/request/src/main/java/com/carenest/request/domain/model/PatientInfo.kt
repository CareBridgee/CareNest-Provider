package com.carenest.request.domain.model


data class PatientInfo(
    val id: String,
    val name: String,
    val image: String,
    val distanceMiles: Float,
    val age: Int,
    val addressLine: String,
    val addressDetail: String,
    val phone: String,
    val summery: String,
)