package com.carenest.provider.earnings.data.repository

import android.util.Log
import com.carenest.provider.designsystem.R
import com.carenest.provider.earnings.data.remote.dto.NurseServiceRequestHistoryDto
import com.carenest.provider.earnings.data.remote.dto.ServiceRequestDetailsResponse
import com.carenest.provider.earnings.domain.model.EarningStatus
import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem
import com.carenest.provider.earnings.domain.repository.EarningsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class EarningsRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : EarningsRepository {

    override suspend fun getEarningsSummary(): Result<EarningsSummary> {
        return runCatching {
            val resolvedItems = fetchResolvedEarningsItems()
            if (resolvedItems.isNotEmpty()) {
                val totalAmount = resolvedItems.sumOf { it.price }
                val jobsCount = resolvedItems.size

                EarningsSummary(
                    totalEarnings = "$${String.format(Locale.US, "%.2f", totalAmount)}",
                    jobsCount = jobsCount,
                    monthName = "This Month"
                )
            } else {
                EarningsSummary(
                    totalEarnings = "$0.00",
                    jobsCount = 0,
                    monthName = "This Month"
                )
            }
        }
    }

    override suspend fun getServiceEarnings(): Result<List<ServiceEarningItem>> {
        return runCatching {
            val resolvedItems = fetchResolvedEarningsItems()
            resolvedItems.map { it.toServiceEarningItem() }
        }
    }

    private data class ResolvedEarningItem(
        val dto: NurseServiceRequestHistoryDto,
        val price: Double,
        val serviceImageUrl: String?,
    )

    private data class ResolvedRequestValues(
        val price: Double,
        val serviceImageUrl: String?,
    )

    private suspend fun fetchResolvedEarningsItems(): List<ResolvedEarningItem> = coroutineScope {
        val history = fetchHistoryFromNetwork()
        history.map { dto ->
            async {
                val resolved = resolveValuesForHistoryItem(dto)
                ResolvedEarningItem(
                    dto = dto,
                    price = resolved.price,
                    serviceImageUrl = resolved.serviceImageUrl,
                )
            }
        }.awaitAll()
    }

    private suspend fun fetchHistoryFromNetwork(): List<NurseServiceRequestHistoryDto> {
        return try {
            val response = httpClient.get("/api/v1/service-requests/nurse/history")
                .body<List<NurseServiceRequestHistoryDto>>()
            Log.d("EarningsRepository", "Fetched ${response.size} history items from network")
            response
        } catch (e: Exception) {
            Log.e("EarningsRepository", "Error fetching nurse service request history", e)
            emptyList()
        }
    }

    private suspend fun resolveValuesForHistoryItem(
        dto: NurseServiceRequestHistoryDto,
    ): ResolvedRequestValues {
        val directImageUrl = listOf(
            dto.serviceImageUrl,
            dto.serviceTypeImageUrl,
        ).firstOrNull { !it.isNullOrBlank() }
        if (dto.estimatedPrice != null && dto.estimatedPrice > 0.0) {
            return ResolvedRequestValues(dto.estimatedPrice, directImageUrl)
        }
        val requestId = dto.serviceRequestId
            ?: return ResolvedRequestValues(0.0, directImageUrl)
        return try {
            val details = httpClient.get("/api/v1/service-requests/$requestId")
                .body<ServiceRequestDetailsResponse>()
            val acceptedOfferPrice = details.offers.firstOrNull { it.status.equals("ACCEPTED", ignoreCase = true) }?.proposedPrice
                ?: details.offers.firstOrNull()?.proposedPrice
            val price = acceptedOfferPrice
                ?: details.serviceType?.basePrice
                ?: details.estimatedPrice
                ?: 0.0
            ResolvedRequestValues(
                price = price,
                serviceImageUrl = directImageUrl
                    ?: details.serviceType?.imageUrl?.takeIf(String::isNotBlank),
            )
        } catch (e: Exception) {
            Log.w("EarningsRepository", "Failed to fetch details for request $requestId to resolve price", e)
            ResolvedRequestValues(0.0, directImageUrl)
        }
    }

    private fun ResolvedEarningItem.toServiceEarningItem(): ServiceEarningItem {
        val dto = this.dto
        val patientFullName = listOfNotNull(dto.patientFirstName, dto.patientLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifEmpty { "Patient" }

        val formattedDuration = if (dto.estimatedDurationMinutes != null && dto.estimatedDurationMinutes > 0) {
            val mins = dto.estimatedDurationMinutes
            if (mins < 60) {
                "$mins mins"
            } else {
                val hours = mins / 60.0
                if (hours % 1.0 == 0.0) {
                    "${hours.toInt()} ${if (hours.toInt() == 1) "hour" else "hours"}"
                } else {
                    "${String.format(Locale.US, "%.1f", hours)} hours"
                }
            }
        } else {
            "N/A"
        }

        val mappedStatus = when (dto.status?.uppercase(Locale.US)) {
            "COMPLETED", "CONFIRMED", "ACCEPTED", "PAID" -> EarningStatus.COMPLETED
            "PENDING", "IN_PROGRESS", "PROCESSING", "ONGOING" -> EarningStatus.PROCESSING
            "CANCELED", "CANCELLED", "REJECTED" -> EarningStatus.CANCELED
            else -> EarningStatus.COMPLETED
        }

        val formattedAmount = "$${String.format(Locale.US, "%.2f", this.price)}"

        val mappedIconRes = when {
            dto.serviceName?.contains("Wound", ignoreCase = true) == true ||
                dto.serviceName?.contains("Inject", ignoreCase = true) == true -> R.drawable.ic_syringe

            dto.serviceName?.contains("Health", ignoreCase = true) == true ||
                dto.serviceName?.contains("Vital", ignoreCase = true) == true ||
                dto.serviceName?.contains("Assessment", ignoreCase = true) == true -> R.drawable.ic_heart_beat

            dto.serviceName?.contains("Med", ignoreCase = true) == true ||
                dto.serviceName?.contains("Pill", ignoreCase = true) == true -> R.drawable.ic_pill

            dto.serviceName?.contains("Physical", ignoreCase = true) == true ||
                dto.serviceName?.contains("Therapy", ignoreCase = true) == true -> R.drawable.ic_physical_therapy

            dto.serviceName?.contains("Elder", ignoreCase = true) == true ||
                dto.serviceName?.contains("Companion", ignoreCase = true) == true -> R.drawable.ic_elderly

            else -> R.drawable.ic_services
        }

        val formattedDate = dto.preferredDate?.takeIf { it.isNotBlank() }
            ?: dto.createdAt?.take(10)
            ?: "Recent"

        return ServiceEarningItem(
            id = dto.serviceRequestId ?: UUID.randomUUID().toString(),
            serviceTitle = dto.serviceName ?: "Nursing Visit",
            patientName = patientFullName,
            date = formattedDate,
            duration = formattedDuration,
            amount = formattedAmount,
            status = mappedStatus,
            iconRes = mappedIconRes,
            serviceTypeId = dto.serviceTypeId,
            serviceImageUrl = serviceImageUrl,
        )
    }
}
