package com.carenest.provider.earnings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.earnings.domain.usecase.GetEarningsSummaryUseCase
import com.carenest.provider.earnings.domain.usecase.GetServiceEarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val getEarningsSummaryUseCase: GetEarningsSummaryUseCase,
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
            val summaryResult = getEarningsSummaryUseCase()
            val listResult = getServiceEarningsUseCase()

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
            ServiceFilter.THIS_MONTH -> {
                val (currentYearMonth, currentMonthShort) = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val now = java.time.LocalDate.now()
                    Pair(java.time.YearMonth.from(now).toString(), now.month.name.take(3))
                } else {
                    val date = java.util.Date()
                    Pair(
                        java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US).format(date),
                        java.text.SimpleDateFormat("MMM", java.util.Locale.US).format(date)
                    )
                }
                currentList.filter { item ->
                    item.date.contains(currentYearMonth, ignoreCase = true) ||
                        item.date.contains(currentMonthShort, ignoreCase = true)
                }
            }
            ServiceFilter.SORT -> currentList.sortedByDescending { item ->
                item.amount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
            }
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
