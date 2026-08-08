package com.carenest.provider.profile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object RegistrationRoute : NavKey

@Serializable
internal data class UnderReviewRoute(val nurseId: String) : NavKey

@Serializable
internal data class ReUploadDocumentRoute(
    val nurseId: String,
    val documentField: String,
    val rejectionReason: String,
) : NavKey
