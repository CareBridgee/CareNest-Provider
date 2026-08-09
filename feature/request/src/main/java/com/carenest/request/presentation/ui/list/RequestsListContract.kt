package com.carenest.request.presentation.ui.list

import com.carenest.request.domain.model.Request

enum class RequestsListModal {
    None,
    EditRate,
    MakeOffer,
    OfferSuccess,
}

data class RequestsListUiState(
    val isLoading: Boolean = true,
    val requests: List<Request> = emptyList(),
    val selectedCardId: String? = null,
    val activeModal: RequestsListModal = RequestsListModal.None,
    val editingRequestId: String? = null,
    val editPriceDraft: Float = 0f,
    val offerRequestId: String? = null,
    val offerCountdown: Int? = null,
    val socketErrorMessage: String? = null,
    val socketErrorCode: String? = null,
)

sealed interface RequestsListIntent {
    data class CardClicked(val requestId: String) : RequestsListIntent
    data class ViewDetailsClicked(val requestId: String) : RequestsListIntent
    data class EditRateClicked(val requestId: String) : RequestsListIntent
    data class MakeOfferClicked(val requestId: String) : RequestsListIntent
    data class EditRateChanged(val rate: Float) : RequestsListIntent
  //  data class SaveDirectRate(val requestId: String, val rate: Float) : RequestsListIntent
    data object SaveRateClicked : RequestsListIntent
    data object DismissModal : RequestsListIntent
}

sealed interface RequestsListEffect {
    data class StartActiveReservationService(val requestId: String) : RequestsListEffect
    data class NavigateToOfferConfirmed(val requestId: String) : RequestsListEffect
    data class NavigateToRequestDetails(val requestId: String) : RequestsListEffect
}
