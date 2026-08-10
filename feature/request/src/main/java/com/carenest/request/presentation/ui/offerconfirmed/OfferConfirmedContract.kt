package com.carenest.request.presentation.ui.offerconfirmed

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.presentation.UiText

data class OfferConfirmedUiState(
    val isLoading: Boolean = true,
    val offer: Offer? = null,
    val cancelDialog: CancelDialogUiState = CancelDialogUiState(),
    val isRequestCancelledByPatient: Boolean = false,
)

data class CancelDialogUiState(
    val isVisible: Boolean = false,
    val selectedReason: CancellationReason? = null,
    val note: String = "",
    val isSubmitting: Boolean = false,
)

sealed interface OfferConfirmedIntent {
    data class Load(val offerId: String) : OfferConfirmedIntent
    data object ViewDetailsClicked : OfferConfirmedIntent
    data object OnShowQrCodeClicked : OfferConfirmedIntent
    data object CancelClicked : OfferConfirmedIntent
    data object OnMessageClicked : OfferConfirmedIntent
    data object OnCallClicked : OfferConfirmedIntent
    data class ReasonSelected(val reason: CancellationReason) : OfferConfirmedIntent
    data class NoteChanged(val note: String) : OfferConfirmedIntent
    data object DismissCancelDialog : OfferConfirmedIntent
    data object ConfirmCancelClicked : OfferConfirmedIntent
    data object DismissRequestCancelledNotice : OfferConfirmedIntent
}

sealed interface OfferConfirmedEffect {
    data class NavigateToDetails(val offerId: String) : OfferConfirmedEffect
    data class NavigateToVisitCompleted(val requestId: String) : OfferConfirmedEffect
    data object NavigateBackToList : OfferConfirmedEffect
    data object NavigateToQrCode : OfferConfirmedEffect
    data class InitiateCall(val phoneNumber: String) : OfferConfirmedEffect
    data class OpenChat(val patientId: String) : OfferConfirmedEffect
    data class ShowError(val message: UiText) : OfferConfirmedEffect
}
