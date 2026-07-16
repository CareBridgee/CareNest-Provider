package com.carenest.provider.designsystem.components.bookings

enum class BookingStatus {
    Upcoming,
    Ongoing,
    Completed,
    Cancelled
}

data class BookingItem(
    val id: String,
    val title: String,
    val patientName: String,
    val status: BookingStatus,
    val dateLabel: String,
    val timeLabel: String,
    val cancelReason: String? = null,
)
