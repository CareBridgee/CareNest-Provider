package com.carenest.provider.payouts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.payouts.domain.usecase.GetPayoutsSummaryUseCase
import com.carenest.provider.payouts.domain.usecase.RequestWithdrawalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayoutsViewModel @Inject constructor(
    private val getPayoutsSummaryUseCase: GetPayoutsSummaryUseCase,
    private val requestWithdrawalUseCase: RequestWithdrawalUseCase
) : ViewModel(),
    StateHolder<PayoutsUiState> by DefaultStateHolder(PayoutsUiState()),
    EffectPublisher<PayoutsEffect> by DefaultEffectPublisher() {

    init {
        loadData()
    }

    fun onIntent(intent: PayoutsIntent) {
        when (intent) {
            PayoutsIntent.WithdrawClicked -> {
                updateState { copy(isWithdrawalModalOpen = true) }
            }
            PayoutsIntent.BackToServiceEarningsClicked -> {
                sendEffect(PayoutsEffect.NavigateBackToEarnings)
            }
            is PayoutsIntent.TabSelected -> {
                updateState { copy(selectedTab = intent.index) }
            }
            PayoutsIntent.RefreshClicked -> loadData()
            PayoutsIntent.DismissModal -> {
                updateState { copy(isWithdrawalModalOpen = false) }
            }
            is PayoutsIntent.ConfirmWithdrawal -> handleWithdrawal(intent.amount)
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true, isError = false, errorMessage = null) }
        viewModelScope.launch {
            val summaryResult = getPayoutsSummaryUseCase.getSummary()
            val historyResult = getPayoutsSummaryUseCase.getHistory()

            if (summaryResult.isSuccess && historyResult.isSuccess) {
                val summary = summaryResult.getOrThrow()
                val history = historyResult.getOrThrow()
                updateState {
                    copy(
                        isLoading = false,
                        summary = summary,
                        historyList = history,
                        isEmpty = history.isEmpty()
                    )
                }
            } else {
                val errorMsg = summaryResult.exceptionOrNull()?.message
                    ?: historyResult.exceptionOrNull()?.message
                    ?: "Failed to load payouts data."
                updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    private fun handleWithdrawal(amount: String) {
        viewModelScope.launch {
            val result = requestWithdrawalUseCase(amount)
            if (result.isSuccess) {
                updateState { copy(isWithdrawalModalOpen = false) }
                sendEffect(PayoutsEffect.ShowToast("Withdrawal request submitted successfully!"))
                loadData()
            } else {
                sendEffect(PayoutsEffect.ShowToast("Withdrawal failed. Please try again."))
            }
        }
    }
}
