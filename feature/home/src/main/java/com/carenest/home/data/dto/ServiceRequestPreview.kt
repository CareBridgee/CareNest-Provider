package com.carenest.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestPreviewDto(
    val patient: PatientPreviewDto? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val serviceImageUrl: String? = null,
    val serviceTypeImageUrl: String? = null,
    val serviceType: ServiceTypePreviewDto? = null,
)

@Serializable
data class ServiceTypePreviewDto(
    val id: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
)

@Serializable
data class PatientPreviewDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImageUrl: String? = null,
)
