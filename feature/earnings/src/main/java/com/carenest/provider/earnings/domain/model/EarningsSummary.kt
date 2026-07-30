package com.carenest.provider.earnings.domain.model

data class EarningsSummary(
    val totalEarnings: String,
    val jobsCount: Int,
    val monthName: String = "This Month"
)
