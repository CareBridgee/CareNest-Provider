package com.carenest.home.domain.model

data class ServiceRequestPreview(
    val patient: PatientPreview?,
)

data class PatientPreview(
    val fullName: String,
    val profileImageUrl: String?,
)
