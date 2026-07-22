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
    val defaultProfileId: String? = null
)

@Serializable
data class ErrorResponseDto(
    val timestamp: String,
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
    val details: String? = null
)
