package com.carenest.provider.payouts.presentation

import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.model.PayoutSummary

data class PayoutsUiState(
    val providerAvatarUrl: String? = null,
    val summary: PayoutSummary = PayoutSummary("$0.00", "$0.00", "$0.00"),
    val historyList: List<PayoutItem> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val isWithdrawalModalOpen: Boolean = false,
    val selectedTab: Int = 4
)

sealed interface PayoutsIntent {
    data object WithdrawClicked : PayoutsIntent
    data object BackToServiceEarningsClicked : PayoutsIntent
    data class TabSelected(val index: Int) : PayoutsIntent
    data object RefreshClicked : PayoutsIntent
    data object DismissModal : PayoutsIntent
    data class ConfirmWithdrawal(val amount: String) : PayoutsIntent
}

sealed interface PayoutsEffect {
    data object NavigateBackToEarnings : PayoutsEffect
    data class ShowToast(val message: String) : PayoutsEffect
}
