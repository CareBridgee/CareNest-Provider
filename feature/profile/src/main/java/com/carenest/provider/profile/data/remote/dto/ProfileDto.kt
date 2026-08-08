package com.carenest.provider.profile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(val profileImageUrl: String? = null)

@Serializable
data class ServiceTypeResponseDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
)

@Serializable
data class NurseResponseDto(
    val id: String,
    val profileImageUrl: String? = null,
    val verificationStatus: String,
    val rejectionReason: String? = null,
    val rejectionDetails: NurseRejectionDetailsDto? = null,
)

@Serializable
data class NurseRejectionDetailsDto(
    val overallReason: String? = null,
    val failedSteps: List<FailedStepDto> = emptyList(),
)

@Serializable
data class FailedStepDto(
    val step: String? = null,
    val reason: String? = null,
)

@Serializable
data class NurseServiceRequestDto(val serviceTypeId: String)

@Serializable
data class NurseServiceBatchResultDto(
    val added: List<NurseServiceResponseDto> = emptyList(),
    val failed: List<BatchFailureDto> = emptyList(),
)

@Serializable
data class NurseServiceResponseDto(
    val id: String? = null,
    val serviceTypeId: String,
)

@Serializable
data class BatchFailureDto(
    val serviceTypeId: String,
    val reason: String? = null,
)

@Serializable
data class ErrorResponseDto(
    val error: String? = null,
    val message: String? = null,
    val details: String? = null,
)
