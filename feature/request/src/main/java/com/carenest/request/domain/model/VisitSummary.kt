package com.carenest.request.domain.model


data class VisitSummary(
    val requestId: String,
    val professionalName: String,
    val serviceType: String,
    val durationMinutes: Int?,
    val completedDate: String,
    val totalAmount: Double?,
    val isVerified: Boolean,
)
