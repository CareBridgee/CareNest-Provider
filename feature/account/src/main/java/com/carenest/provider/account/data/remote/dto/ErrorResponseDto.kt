package com.carenest.provider.account.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val message: String? = null,
    val error: String? = null,
    val details: String? = null,
)
