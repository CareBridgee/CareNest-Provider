package com.carenest.provider.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val phoneNumber: String
)

@Serializable
data class VerifyOtpRequestDto(
    val phoneNumber: String,
    val otp: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val user: UserDto? = null,
    val message: String? = null
)

@Serializable
data class UserDto(
    val id: String,
    val phoneNumber: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val lastLoginAt: String? = null,
    val defaultProfileId: String? = null,
    val nurse: NurseAuthDto? = null
)

@Serializable
data class NurseAuthDto(
    val id: String,
    val profileImageUrl: String? = null,
    val nationalId: String? = null,
    val nationalIdFrontUrl: String? = null,
    val nationalIdBackUrl: String? = null,
    val licenseImageUrl: String? = null,
    val professionalCertificateUrl: String? = null,
    val specialization: String? = null,
    val yearsOfExperience: Int? = null,
    val bio: String? = null,
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
data class CurrentUserDto(
    val profileCompleted: Boolean,
    val nurse: NurseAuthDto? = null,
)

@Serializable
data class DevLoginResponseDto(
    val phoneNumber: String,
    val otp: String
)

@Serializable
data class ErrorResponseDto(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null,
    val details: String? = null
)
