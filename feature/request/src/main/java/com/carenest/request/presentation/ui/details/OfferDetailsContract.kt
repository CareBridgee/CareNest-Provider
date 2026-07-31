package com.carenest.request.presentation.ui.details

import com.carenest.request.domain.model.Offer

data class OfferDetailsUiState(
    val isLoading: Boolean = true,
    val offer: Offer? = null,
)

sealed interface OfferDetailsIntent {
    data class Load(val requestId: String) : OfferDetailsIntent
    data object BackClicked : OfferDetailsIntent
    data object CallClicked : OfferDetailsIntent
    data object MessageClicked : OfferDetailsIntent
    data object CopyAddressClicked : OfferDetailsIntent
    data object ViewSummaryClicked : OfferDetailsIntent
    data object OpenInMapsClicked : OfferDetailsIntent
    data object MoreClicked : OfferDetailsIntent
}

sealed interface OfferDetailsEffect {
    data object NavigateBack : OfferDetailsEffect
    data class InitiateCall(val phone: String) : OfferDetailsEffect
    data class OpenChat(val patientId: String) : OfferDetailsEffect
    data class CopyToClipboard(val text: String) : OfferDetailsEffect
    data class ShowSummary(val summary: String) : OfferDetailsEffect
    data class OpenMaps(val address: String) : OfferDetailsEffect
}
