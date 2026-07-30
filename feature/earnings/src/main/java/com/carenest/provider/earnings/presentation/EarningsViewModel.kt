package com.carenest.provider.earnings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.earnings.domain.usecase.GetServiceEarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val getServiceEarningsUseCase: GetServiceEarningsUseCase
) : ViewModel(),
    StateHolder<EarningsUiState> by DefaultStateHolder(EarningsUiState()),
    EffectPublisher<EarningsEffect> by DefaultEffectPublisher() {

    init {
        loadData()
    }

    fun onIntent(intent: EarningsIntent) {
        when (intent) {
            is EarningsIntent.FilterSelected -> handleFilterSelected(intent.filter)
            EarningsIntent.ViewPayoutsClicked -> {
                sendEffect(EarningsEffect.NavigateToPayouts)
            }
            is EarningsIntent.TabSelected -> {
                updateState { copy(selectedTab = intent.index) }
            }
            EarningsIntent.RefreshClicked -> loadData()
        }
    }

    private fun loadData() {
        updateState { copy(isLoading = true, isError = false, errorMessage = null) }
        viewModelScope.launch {
            val summaryResult = getServiceEarningsUseCase.getSummary()
            val listResult = getServiceEarningsUseCase.getEarningsList()

            if (summaryResult.isSuccess && listResult.isSuccess) {
                val summary = summaryResult.getOrThrow()
                val list = listResult.getOrThrow()
                updateState {
                    copy(
                        isLoading = false,
                        summary = summary,
                        serviceEarnings = list,
                        filteredEarnings = list,
                        isEmpty = list.isEmpty()
                    )
                }
            } else {
                val errorMsg = summaryResult.exceptionOrNull()?.message
                    ?: listResult.exceptionOrNull()?.message
                    ?: "Failed to load service earnings."
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

    private fun handleFilterSelected(filter: ServiceFilter) {
        val currentList = currentState.serviceEarnings
        val filtered = when (filter) {
            ServiceFilter.ALL_SERVICES -> currentList
            ServiceFilter.THIS_MONTH -> currentList.filter { it.date.contains("Oct") }
            ServiceFilter.SORT -> currentList.sortedByDescending { it.amount }
        }
        updateState {
            copy(
                selectedFilter = filter,
                filteredEarnings = filtered,
                isEmpty = filtered.isEmpty()
            )
        }
    }
}
