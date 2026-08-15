package com.carenest.home.domain.model

data class ServiceRequestPreview(
    val patient: PatientPreview?,
    val serviceTypeId: String?,
    val serviceName: String?,
    val serviceImageUrl: String?,
)

data class PatientPreview(
    val fullName: String,
    val profileImageUrl: String?,
)
