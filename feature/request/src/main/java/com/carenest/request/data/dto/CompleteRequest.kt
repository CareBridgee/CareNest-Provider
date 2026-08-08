package com.carenest.request.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompleteRequest(
    val visitCode: String
)