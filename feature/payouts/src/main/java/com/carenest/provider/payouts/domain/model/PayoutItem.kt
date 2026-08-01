package com.carenest.provider.payouts.domain.model

enum class PayoutStatus {
    PENDING,
    COMPLETED,
    FAILED
}

data class PayoutItem(
    val id: String,
    val methodTitle: String,
    val dateTime: String,
    val amount: String,
    val status: PayoutStatus,
    val iconRes: Int
)
