package com.carenest.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestPreviewDto(
    val patient: PatientPreviewDto? = null,
)

@Serializable
data class PatientPreviewDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null,
)
