package com.carenest.home.presentation.home

import com.carenest.home.domain.model.NurseRequest

enum class ActiveModal {
    None,
    EditRate,
    MakeOffer,
    OfferSuccess,
}

data class HomeUiState(
    val isOnline: Boolean = false,
    val isLoading: Boolean = false,
    val requests: List<NurseRequest> = emptyList(),
    val selectedCardId: String? = null,
    val activeModal: ActiveModal = ActiveModal.None,
    val editingRequestId: String? = null,
    val editRateDraft: Float = 0f,
    val offerRequestId: String? = null,
    val offerCountdown: Int? = null,
    val offerWillAccept: Boolean = false,
    val offerAcceptAtSecond: Int = 10,
)

sealed interface HomeIntent {
    data class OnlineToggled(val isOnline: Boolean) : HomeIntent
    data class CardClicked(val requestId: String) : HomeIntent
    data class EditRateClicked(val requestId: String) : HomeIntent
    data class MakeOfferClicked(val requestId: String) : HomeIntent
    data class EditRateChanged(val rate: Float) : HomeIntent
    data object SaveRateClicked : HomeIntent
    data object DismissModal : HomeIntent
}
