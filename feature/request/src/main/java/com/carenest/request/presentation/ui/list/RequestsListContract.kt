package com.carenest.request.presentation.ui.list

import com.carenest.request.domain.model.NurseRequest

enum class RequestsListModal {
    None,
    EditRate,
    MakeOffer,
}

data class RequestsListUiState(
    val isLoading: Boolean = true,
    val requests: List<NurseRequest> = emptyList(),
    val selectedCardId: String? = null,
    val activeModal: RequestsListModal = RequestsListModal.None,
    val editingRequestId: String? = null,
    val editRateDraft: Float = 0f,
    val offerRequestId: String? = null,
    val offerCountdown: Int? = null,
)

sealed interface RequestsListIntent {
    data class CardClicked(val requestId: String) : RequestsListIntent
    data class EditRateClicked(val requestId: String) : RequestsListIntent
    data class MakeOfferClicked(val requestId: String) : RequestsListIntent
    data class EditRateChanged(val rate: Float) : RequestsListIntent
    data object SaveRateClicked : RequestsListIntent
    data object DismissModal : RequestsListIntent
}

sealed interface RequestsListEffect {
    data class NavigateToOfferConfirmed(val requestId: String) : RequestsListEffect
}
