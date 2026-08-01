package com.carenest.provider.earnings.domain.model

enum class EarningStatus {
    COMPLETED,
    PROCESSING,
    CANCELED
}

data class ServiceEarningItem(
    val id: String,
    val serviceTitle: String,
    val patientName: String,
    val date: String,
    val duration: String,
    val amount: String,
    val status: EarningStatus,
    val iconRes: Int
)
