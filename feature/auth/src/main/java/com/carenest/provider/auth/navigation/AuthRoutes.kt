package com.carenest.provider.auth.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object LoginRoute : NavKey

@Serializable
internal data class OtpRoute(
    val phone: String,
    val otp: String? = null,
    val pendingToken: String? = null,
) : NavKey
