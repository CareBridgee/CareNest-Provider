package com.carenest.home.domain.model

data class EarningsSummary(
    val todayEarnings: Double,
    val changePercent: Double,
    val jobsToday: Int,
    val rating: Double,
    val reviewCount: Int = 0,
)