package com.carenest.chat.presentation.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.chat.domain.model.ChatMessage

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
private val zone = ZoneId.systemDefault()
@RequiresApi(Build.VERSION_CODES.O)
private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

@RequiresApi(Build.VERSION_CODES.O)
fun ChatMessage.dayKey(): LocalDate =
    Instant.ofEpochMilli(sentAtEpochMillis).atZone(zone).toLocalDate()

@RequiresApi(Build.VERSION_CODES.O)
fun formatMessageTime(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(zone).format(timeFormatter)

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateSeparator(
    epochMillis: Long,
    todayLabel: String,
    yesterdayLabel: String,
): String {
    val messageDate = Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    return when (messageDate) {
        today -> todayLabel
        today.minusDays(1) -> yesterdayLabel
        else -> messageDate.format(dateFormatter)
    }
}
