package com.carenest.home.data.mapper

import com.carenest.home.data.dto.PatientPreviewDto
import com.carenest.home.data.dto.ServiceRequestPreviewDto
import com.carenest.home.domain.model.PatientPreview
import com.carenest.home.domain.model.ServiceRequestPreview

fun ServiceRequestPreviewDto.toDomain(): ServiceRequestPreview = ServiceRequestPreview(
    patient = patient?.toDomain(),
    serviceTypeId = serviceType?.id?.takeIf(String::isNotBlank)
        ?: serviceTypeId?.takeIf(String::isNotBlank),
    serviceName = serviceType?.name?.takeIf(String::isNotBlank)
        ?: serviceName?.takeIf(String::isNotBlank),
    serviceImageUrl = listOf(
        serviceType?.imageUrl,
        serviceImageUrl,
        serviceTypeImageUrl,
    ).firstOrNull { !it.isNullOrBlank() },
)

private fun PatientPreviewDto.toDomain(): PatientPreview = PatientPreview(
    fullName = listOf(firstName, lastName)
        .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
        .joinToString(" "),
    profileImageUrl = profileImageUrl,
)
