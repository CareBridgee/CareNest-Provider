package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class NearbyNurseServiceRequestResponse(
    val serviceRequestId: String,
    val profileId: String,
    val patientFirstName: String? = null,
    val patientLastName: String? = null,
    val patientProfileImageUrl: String? = null,
    val serviceTypeId: String? = null,
    val serviceName: String? = null,
    val serviceDescription: String? = null,
    val preferredDate: String? = null,
    val preferredTime: String? = null,
    val status: String? = null,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double? = null,
    val estimatedPrice: Double? = null,
    val estimatedDurationMinutes: Int? = null,
    val createdAt: String? = null
)

fun NearbyNurseServiceRequestResponse.patientDisplayName(
    fallback: String = "Patient Request",
): String = listOf(patientFirstName, patientLastName)
    .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
    .joinToString(" ")
    .ifBlank { fallback }
