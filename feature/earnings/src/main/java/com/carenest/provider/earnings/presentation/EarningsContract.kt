package com.carenest.provider.earnings.presentation

import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem

enum class ServiceFilter {
    ALL_SERVICES,
    THIS_MONTH,
    SORT
}

data class EarningsUiState(
    val summary: EarningsSummary = EarningsSummary(totalEarnings = "$0.00", jobsCount = 0),
    val serviceEarnings: List<ServiceEarningItem> = emptyList(),
    val filteredEarnings: List<ServiceEarningItem> = emptyList(),
    val selectedFilter: ServiceFilter = ServiceFilter.ALL_SERVICES,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val selectedTab: Int = 4
)

sealed interface EarningsIntent {
    data class FilterSelected(val filter: ServiceFilter) : EarningsIntent
    data object ViewPayoutsClicked : EarningsIntent
    data class TabSelected(val index: Int) : EarningsIntent
    data object RefreshClicked : EarningsIntent
}

sealed interface EarningsEffect {
    data object NavigateToPayouts : EarningsEffect
}
