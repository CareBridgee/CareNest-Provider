package com.carenest.home.data.datasource

import com.carenest.home.data.dto.ServiceRequestPreviewDto
import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.model.RequestStatus
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.NearbyNurseServiceRequestResponse
import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.provider.core.network.socket.model.patientDisplayName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val nurseSocketClient: NurseSocketClient
) : NurseRequestsDataSource {

    override suspend fun getIncomingRequests(): List<NurseRequest> {
        return try {
            val response = httpClient.get("api/v1/service-requests/nearby")
                .body<List<NearbyNurseServiceRequestResponse>>()
            response.map { item ->
                NurseRequest(
                    id = item.serviceRequestId,
                    patientName = item.patientDisplayName(),
                    patientImage = item.patientProfileImageUrl ?: "",
                    serviceType = item.serviceName ?: "Nursing Visit",
                    serviceImage = "",
                    baseRate = (item.estimatedPrice ?: 50.0).toFloat(),
                    distanceMiles = (item.distanceKm ?: 0.0).toFloat(),
                    status = RequestStatus.ESTIMATED
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String?
    ) {
        nurseSocketClient.createOffer(
            serviceRequestId = serviceRequestId,
            proposedPrice = proposedPrice,
            proposedDate = proposedDate,
            proposedTime = proposedTime,
            message = message
        )
    }

    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> {
        return nurseSocketClient.reservationEvents.filter { event ->
            event.reservationId == reservationId || event.reservationId.isNullOrEmpty()
        }
    }

    override suspend fun getEarningsSummary(): EarningsSummary {
        return try {
            httpClient.get("api/v1/nurse/earnings").body<EarningsSummary>()
        } catch (e: Exception) {
            EarningsSummary(
                todayEarnings = 0.0,
                changePercent = 0.0,
                jobsToday = 0,
                rating = 5.0
            )
        }
    }

    override suspend fun getNurseProfile(): NurseProfile {
        return try {
            httpClient.get("api/v1/profile").body<NurseProfile>()
        } catch (e: Exception) {
            NurseProfile(
                name = "Care Provider",
                avatarUrl = ""
            )
        }
    }

   override suspend fun getServiceRequestPreview(serviceRequestId: String): ServiceRequestPreviewDto =
        httpClient.get("/api/v1/service-requests/$serviceRequestId/preview")
            .body<ServiceRequestPreviewDto>()
}
