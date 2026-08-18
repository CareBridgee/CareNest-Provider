package com.carenest.provider.payouts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.payouts.domain.usecase.GetPayoutSummaryUseCase
import com.carenest.provider.payouts.domain.usecase.GetWithdrawHistoryUseCase
import com.carenest.provider.payouts.domain.usecase.RequestWithdrawalUseCase
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class PayoutsViewModel @Inject constructor(
    private val getPayoutSummaryUseCase: GetPayoutSummaryUseCase,
    private val getWithdrawHistoryUseCase: GetWithdrawHistoryUseCase,
    private val requestWithdrawalUseCase: RequestWithdrawalUseCase,
    private val authenticationSessionStore: AuthenticationSessionStore,
    private val getNurse: GetNurseUseCase,
) : ViewModel(),
    StateHolder<PayoutsUiState> by DefaultStateHolder(PayoutsUiState()),
    EffectPublisher<PayoutsEffect> by DefaultEffectPublisher() {

    init {
        loadProviderAvatar()
        loadData()
    }

    private fun loadProviderAvatar() {
        viewModelScope.launch {
            val nurseId = authenticationSessionStore.session.first()?.nurseId ?: return@launch
            getNurse(nurseId).onSuccess { profile ->
                updateState { copy(providerAvatarUrl = profile.profileImageUrl) }
            }
        }
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
            val summaryResult = getPayoutSummaryUseCase()
            val historyResult = getWithdrawHistoryUseCase()

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
                val error = summaryResult.exceptionOrNull() ?: historyResult.exceptionOrNull()
                updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = error?.userMessage() ?: "Failed to load payouts data."
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
                val message = result.exceptionOrNull()?.userMessage() ?: "Withdrawal failed. Please try again."
                sendEffect(PayoutsEffect.ShowToast(message))
            }
        }
    }

    private fun Throwable.userMessage(): String {
        return when (this) {
            is java.net.UnknownHostException, is java.net.ConnectException -> "Please check your internet connection and try again."
            else -> "Something went wrong. Please try again."
        }
    }
}
