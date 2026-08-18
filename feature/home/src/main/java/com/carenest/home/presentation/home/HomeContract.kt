package com.carenest.home.presentation.home

import androidx.annotation.StringRes
import com.carenest.home.domain.model.NurseRequest
import com.carenest.provider.designsystem.components.toast.ToastType

enum class ActiveModal {
    None,
    EditRate,
    MakeOffer,
    OfferSuccess,
    RequestCancelled,
}

data class HomeUiState(
    val nurseName : String = "",
    val nurseAvatar : String?=null,
    val isProviderApproved: Boolean = false,
    val isOnline: Boolean = false,
    val isGettingLocation: Boolean = false,
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
    val earnings : Double = 0.0,
    val changePercent : Double = 0.0,
    val jobsToday : Int = 0,
    val rating : Double = 0.0,
    val selectedTab: Int = 0,
    val socketErrorMessage: String? = null,
    val socketErrorCode: String? = null,
)

sealed interface HomeIntent {
    data class OnlineToggled(val isOnline: Boolean) : HomeIntent
    data class CardClicked(val requestId: String) : HomeIntent
    data class EditRateClicked(val requestId: String) : HomeIntent
    data class MakeOfferClicked(val requestId: String) : HomeIntent
    data class EditRateChanged(val rate: Float) : HomeIntent
    data class TabSelected(val index: Int) : HomeIntent
    data object ViewAllRequestsClicked : HomeIntent
    data object RefreshProfile : HomeIntent
    data object SaveRateClicked : HomeIntent
    data object DismissModal : HomeIntent
}

sealed interface HomeEffect {
    data object NavigateToRequestList : HomeEffect
    data class NavigateToOfferConfirmed(val requestId: String) : HomeEffect
    data class StartActiveReservationService(val requestId: String) : HomeEffect
    data class ShowSnackbarRes(
        @param:StringRes val messageRes: Int,
        val type: ToastType = ToastType.Info,
    ) : HomeEffect
    data class ShowSnackbarString(
        val message: String,
        val type: ToastType = ToastType.Info,
    ) : HomeEffect
}

