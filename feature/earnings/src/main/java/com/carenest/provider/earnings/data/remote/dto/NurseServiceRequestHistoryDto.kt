package com.carenest.provider.earnings.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class NurseServiceRequestHistoryDto(
    val serviceRequestId: String? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val estimatedDurationMinutes: Int? = null,
    val patientProfileId: String? = null,
    val patientFirstName: String? = null,
    val patientLastName: String? = null,
    val patientPhoneNumber: String? = null,
    val patientProfileImageUrl: String? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: JsonElement? = null,
    val status: String? = null,
    val estimatedPrice: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
