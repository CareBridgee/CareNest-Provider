package com.carenest.request.presentation.ui.offerconfirmed

import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.VisitCode
import com.carenest.request.presentation.UiText

data class OfferConfirmedUiState(
    val isLoading: Boolean = true,
    val offer: Offer? = null,
    val isGeneratingVisitCode: Boolean = false,
    val cancelDialog: CancelDialogUiState = CancelDialogUiState(),
)

data class CancelDialogUiState(
    val isVisible: Boolean = false,
    val isSubmitting: Boolean = false,
)

sealed interface OfferConfirmedIntent {
    data class Load(val offerId: String) : OfferConfirmedIntent
    data object ViewDetailsClicked : OfferConfirmedIntent
    data object OnShowQrCodeClicked : OfferConfirmedIntent
    data object CancelClicked : OfferConfirmedIntent
    data object OnMessageClicked : OfferConfirmedIntent
    data object OnCallClicked : OfferConfirmedIntent
    data object DismissCancelDialog : OfferConfirmedIntent
    data object ConfirmCancelClicked : OfferConfirmedIntent
}

sealed interface OfferConfirmedEffect {
    data class NavigateToDetails(val offerId: String) : OfferConfirmedEffect
    data class NavigateToVisitCompleted(val requestId: String) : OfferConfirmedEffect
    data object NavigateBackToList : OfferConfirmedEffect
    data class NavigateToQrCode(val visitCode: VisitCode) : OfferConfirmedEffect
    data class InitiateCall(val phoneNumber: String) : OfferConfirmedEffect
    data class OpenChat(val patientId: String) : OfferConfirmedEffect
    data class ShowError(val message: UiText) : OfferConfirmedEffect
}
