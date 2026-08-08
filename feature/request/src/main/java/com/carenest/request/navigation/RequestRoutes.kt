package com.carenest.request.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RequestRoutes : NavKey {
    @Serializable
    data object RequestList : RequestRoutes

    @Serializable
    data class OfferConfirmed(val requestId: String) : RequestRoutes

    @Serializable
    data class RequestDetails(val requestId: String) : RequestRoutes

    @Serializable
    data class VisitCompleted(val requestId: String) : RequestRoutes

    @Serializable
    data class ScanQr(val requestId: String) : RequestRoutes
}
