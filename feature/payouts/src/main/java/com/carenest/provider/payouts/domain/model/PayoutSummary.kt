package com.carenest.provider.payouts.domain.model

data class PayoutSummary(
    val availableBalance: String,
    val pendingAmount: String,
    val thisMonthAmount: String
)
