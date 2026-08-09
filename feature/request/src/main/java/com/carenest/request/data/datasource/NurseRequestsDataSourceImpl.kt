package com.carenest.request.data.datasource

import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.NearbyNurseServiceRequestResponse
import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientInfo
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.RequestStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val nurseSocketClient: NurseSocketClient
) : NurseRequestsDataSource {

    override suspend fun getIncomingRequests(): List<Request> {
        return try {
            val response = httpClient.get("api/v1/service-requests/nearby").body<List<NearbyNurseServiceRequestResponse>>()
            response.map { item ->
                Request(
                    id = item.serviceRequestId,
                    patientName = item.serviceName ?: "Patient Request",
                    serviceName = item.serviceName ?: "Nursing Visit",
                    basePrice = (item.estimatedPrice ?: 50.0).toFloat(),
                    patientAddress = item.distanceKm?.let { "$it km" } ?: "Nearby",
                    serviceImage = "",
                    status = RequestStatus.ESTIMATED
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRequestContract(requestId: String): Offer {
        delay(300)
        return Offer(
            offerId = requestId,
            visitDate = "Today",
            visitTime = "Scheduled",
            distanceMiles = 2.4f,
            estimatedArrival = "10:30 AM",
            estimatedDuration = "45 mins",
            patientInfo = PatientInfo(
                id = "pat-001",
                name = "Patient",
                age = 65,
                image = "",
                phone = "",
                addressLine = "Patient Address",
                addressDetail = "",
                summery = "Care Visit",
                distanceMiles = 2.4f
            ),
            totalAmount = 50f,
            serviceType = "Nursing Visit",
            serviceImage = ""
        )
    }

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean {
        return try {
            nurseSocketClient.cancelReservation(requestId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean {
        return true
    }

    override suspend fun createOffer(requestId: String, proposedPrice: Double, message: String?) {
        nurseSocketClient.connect()
        nurseSocketClient.subscribeToReservation(requestId)
        nurseSocketClient.createOffer(
            serviceRequestId = requestId,
            proposedPrice = proposedPrice,
            proposedDate = "2026-08-15",
            proposedTime = "10:00",
            message = message ?: "Offer submitted by nurse"
        )
    }

    override suspend fun withdrawOffer(offerId: String) {
        nurseSocketClient.withdrawOffer(offerId)
    }

    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> {
        return nurseSocketClient.reservationEvents.filter { event ->
            val id = event.effectiveReservationId
            id == reservationId || id.isNullOrEmpty()
        }
    }
}