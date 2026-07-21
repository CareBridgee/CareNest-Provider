package com.carenest.provider.profile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object RegistrationRoute : NavKey

@Serializable
internal data object UnderReviewRoute : NavKey

@Serializable
internal data object ReUploadDocumentRoute : NavKey
